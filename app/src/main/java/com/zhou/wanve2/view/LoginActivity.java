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

import org.json.JSONException;
import org.json.JSONObject;
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
import java.util.ArrayList;
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
    @BindView(R.id.et_user)
    EditText et_user;
    @BindView(R.id.et_psd)
    EditText et_psd;
    @BindView(R.id.cb_remember)
    CheckBox cb_remember;
    @BindView(R.id.cb_automaticity)
    CheckBox cb_automaticity;

    private UserBean userBean;
    private SharedPreferences sp;
    private UserBean bean;

    @Override
    public int getLayout() {
        return R.layout.activity_login;
    }

    @Override
    public void init() {
        initLogin();//判断是否自动登录
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initSp();
    }

    /**
     * 判断是否记住密码与自动登录
     */
    private void initLogin() {
        bean = (UserBean) SpUtil.getObject(getApplicationContext(), "userBean", UserBean.class);
        if (bean != null && bean.isAtomatic()) {
            FirstLogin();
            return;
        }
        if (bean != null && bean.isRemember()) {
            et_user.setText(bean.getUser());
            et_psd.setText(bean.getPsd());
            cb_remember.setChecked(bean.isRemember());
        }
    }

    /**
     * 获取缓存
     */
    private void initSp() {
        userBean = new UserBean();
        sp = getSharedPreferences("user", Context.MODE_PRIVATE);

        cb_remember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                userBean.setRemember(isChecked);
            }
        });
        cb_automaticity.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                userBean.setAtomatic(isChecked);
            }
        });
    }

    @OnClick({R.id.bt_login})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_login:
                FirstLogin();//初次用户登录
                break;
        }
    }

    /**
     * 初次用户登录
     * <p>
     * 仅用于验证当前用户是否属于本系统用户
     */
    private void FirstLogin() {
        final String etUser;
        final String etPsd;
        if (bean != null && bean.isAtomatic()) {
            etUser = bean.getUser();
            etPsd = bean.getPsd();
        } else {
            etUser = et_user.getText().toString().trim();
            etPsd = et_psd.getText().toString().trim();
        }
        if (TextUtils.isEmpty(etUser)) {
            ToastUtil.show(getApplicationContext(), "用户名不能为空");
            return;
        }
        if (TextUtils.isEmpty(etPsd)) {
            ToastUtil.show(getApplicationContext(), "用户名不能为空");
            return;
        }
        dialog.show();
        final String loginUrl = Constant.BASE_URL + Constant.HUAMBO_LOGIN_URL + "{UserID:'" + etUser + "'" + ",UserPsw:'" + etPsd + "'}";//登录验证地址
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder().url(loginUrl).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                dialog.dismiss();
                Log.d(TAG, "onFailure: " + e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.show(getApplicationContext(), "请检查您的网络是否开启");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                Log.d(TAG, "onResponse: 第一次登录成功 " + string);
                try {
                    final JSONObject jsonObject = new JSONObject(string);
                    boolean result = jsonObject.getBoolean("Result");
                    if (result) {
                        userBean.setUser(etUser);
                        userBean.setPsd(etPsd);
                        SpUtil.putObject(getApplicationContext(), "userBean", userBean);
                        startToActivity(MainActivity.class);
                        // 判断是否登录成功  进行服务器验证  验证地址是否正确同时跳转 此为第二次登录
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    ToastUtil.show(getApplicationContext(), jsonObject.getString("Message"));
                                    dialog.dismiss();
                                    return;
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * 本应用的第三次验证，文档的第二次请求        不行
     */
    private void ThridLogin(final String websession) {
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder().url(Constant.iniUrl + websession).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: 第三次失败" + e.getMessage());
                dialog.dismiss();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "onResponse: 第三次成功");
                //startActivity(MainActivity.newIntent(getApplicationContext(), websession));
                //startToActivity(MainActivity.class);
                finish();
                dialog.dismiss();
            }
        });
    }

}
