package com.zhou.wanve2.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.zhou.wanve2.R;
import com.zhou.wanve2.TwoBean;
import com.zhou.wanve2.UserBean;
import com.zhou.wanve2.base.BaseActivity;
import com.zhou.wanve2.base.Constant;
import com.zhou.wanve2.util.SpUtil;
import com.zhou.wanve2.util.ToastUtil;
import com.zhou.wanve2.util.WebServiceUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends BaseActivity {

    private static final String TAG = "LoginActivity";
    @BindView(R.id.et_user) EditText et_user;
    @BindView(R.id.et_psd) EditText et_psd;
    @BindView(R.id.cb_remember) CheckBox cb_remember;
    @BindView(R.id.cb_automaticity) CheckBox cb_automaticity;


    private boolean isRemember;
    private boolean isAutomaticity;
    private String etUser;
    private String etPsd;


    @Override
    public int getLayout() {
        return R.layout.activity_login;
    }

    @Override
    public void init() {
        initCheckBox();
        initInfo();
    }

    //勾选框逻辑
    private void initCheckBox() {
        cb_remember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isRemember = isChecked;
            }
        });
        cb_automaticity.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isAutomaticity = isChecked;
                cb_remember.setChecked(isChecked);
            }
        });
    }

    /**
     * 获取缓存
     */
    private void initInfo() {
        UserBean userBean = (UserBean) SpUtil.getObject(this,Constant.UserBean,UserBean.class);
        if (userBean != null && userBean.isAtomatic()){
            etUser = userBean.getUser();
            etPsd = userBean.getPsd();
            FirstLogin(userBean);

        }
        if (userBean != null && userBean.isRemember()){
            cb_remember.setChecked(userBean.isRemember());
            et_user.setText(userBean.getUser());
            et_psd.setText(userBean.getPsd());
        }
    }

    @OnClick({R.id.bt_login})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_login:
                LoginWebServer();
                break;
        }
    }

    /**
     * 初次登录
     * <p>
     * 使用webServer
     */
    private void LoginWebServer() {
        final UserBean userBean = new UserBean();
        etUser = et_user.getText().toString().trim();
        etPsd = et_psd.getText().toString().trim();
        userBean.setPsd(etPsd);
        userBean.setUser(etUser);
        userBean.setRemember(isRemember);
        userBean.setAtomatic(isAutomaticity);
        Log.d(TAG, "et_psd: " + etPsd);
        if (TextUtils.isEmpty(etUser)) {
            ToastUtil.show(getApplicationContext(), "用户名不能为空");
            return;
        }
        if (TextUtils.isEmpty(etPsd)) {
            ToastUtil.show(getApplicationContext(), "用户名不能为空");
            return;
        }
        FirstLogin(userBean);//初次都能来
    }

    /**
     * 初次登录
     * @param userBean
     */
    private void FirstLogin(final UserBean userBean) {
        HashMap<String, String> data = new HashMap<>();
        data.put("userID", etUser);
        data.put("userPSW", etPsd);
        dialog.show();
        WebServiceUtil.callWebService(Constant.LOGIN_URL, "CheckUserLogin", data, new WebServiceUtil.WebServiceCallBack() {
            @Override
            public void callBack(SoapObject result) {
                Log.d(TAG, "callBack: " + Constant.LOGIN_URL);
                if (result != null) {
                    Log.d(TAG, "callBack: " + result.toString());
                    String login = result.toString();
                    String substring = login.substring(44);
                    if (substring.contains("ok")) {
                        SpUtil.putObject(LoginActivity.this,Constant.UserBean,userBean);
                        TwoLogin(etUser);
                        return;
                    } else if (substring.contains("fail")) {
                        ToastUtil.show(getApplicationContext(), "密码错误");
                        dialog.dismiss();
                        return;
                    } else if (substring.contains("noExists")) {
                        ToastUtil.show(getApplicationContext(), "账号不存在");
                        dialog.dismiss();
                        return;
                    } else if (substring.contains("isLock")) {
                        ToastUtil.show(getApplicationContext(), "账号被锁");
                        dialog.dismiss();
                        return;
                    } else if (substring.contains("sysErr")) {
                        ToastUtil.show(getApplicationContext(), "系统异常");
                        dialog.dismiss();
                        return;
                    } else if (substring.contains("noOpenUse")) {
                        ToastUtil.show(getApplicationContext(), "账号被禁用");
                        dialog.dismiss();
                        return;
                    }
                } else {
                    ToastUtil.show(getApplicationContext(), "请求失败");
                }
            }
        });
    }

    /**
     * 第二次验证登录
     * <p>
     * 验证服务器是否变更
     */
    private void TwoLogin(final String user) {
        MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/xml; charset=utf-8");//根据C#大牛那边写的头文件 以及登录验证方式
        OkHttpClient okHttpClient = new OkHttpClient();
        String psw = URLEncoder.encode(Constant.PSW);
        Log.d(TAG, "加密后的密码: " + psw);
        final String strXML = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>" +
                "<REQUEST>" +
                "<SP_ID>ToWanPic</SP_ID>" +
                "<PASSWORD>" + psw + "</PASSWORD>" +
                "<USER>"+user+"</USER>" +
                "</REQUEST>";
        Request request = new Request.Builder().url(Constant.ssoUrl)
                .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, strXML)).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("", "onFailure: 第二次登录失败 " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                SpUtil.putString(getApplicationContext(),Constant.UserName,user);
                Log.d(TAG, "onResponse: 第二次登录成功" + string);
                try {
                    parseXMLWithPull(string);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //xml解析
    public void parseXMLWithPull(String xmlData) throws Exception {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(new StringReader(xmlData));
        int eventType = parser.getEventType();
        String resp_code = "";
        String resp_desc = "";
        String websession = "";

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String nodeName = parser.getName();
            //Log.d(TAG, "parseXMLWithPull: "+nodeName);
            switch (eventType) {
                // 开始解析某个结点
                case XmlPullParser.START_TAG: {
                    if ("WEBSESSION".equals(nodeName)) {
                        websession = parser.nextText();
                    } else if ("RESP_CODE".equals(nodeName)) {
                        resp_code = parser.nextText();
                    } else if ("RESP_DESC".equals(nodeName)) {
                        resp_desc = parser.nextText();
                    }
                    break;
                }
                // 完成解析某个结点
                case XmlPullParser.END_TAG: {
                    if ("RESPONSE".equals(nodeName)) {
                        Log.d("MainActivity", "WEBSESSION is " + websession);
                        Log.d("MainActivity", "RESP_CODE is " + resp_code);
                        Log.d("MainActivity", "RESP_DESC is " + resp_desc);
                        if (resp_code.equals("0000")) {
                            //最后一次验证，即为第三次验证
                            startActivity(MainActivity.newIntent(getApplicationContext(), Constant.iniUrl + websession));
                            dialog.dismiss();
                            finish();
                        } else {
                            final String des = resp_desc;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.show(getApplicationContext(), des);
                                    dialog.dismiss();
                                }
                            });
                        }
                    }
                    break;
                }
                default:
                    break;
            }
            eventType = parser.next();
        }
    }

}