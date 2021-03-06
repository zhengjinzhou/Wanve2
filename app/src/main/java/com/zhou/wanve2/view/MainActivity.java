package com.zhou.wanve2.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.zhou.wanve2.R;
import com.zhou.wanve2.base.BaseActivity;
import com.zhou.wanve2.base.Constant;
import com.zhou.wanve2.util.ToastUtil;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    @BindView(R.id.tv_index) TextView tv_index;
    @BindView(R.id.tv_map) TextView tv_map;
    @BindView(R.id.tv_sum) TextView tv_sum;
    @BindView(R.id.tv_center) TextView tv_center;
    @BindView(R.id.webView) WebView webView;

    private String websession;
    private String[] brief_url;


    @Override
    public int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void init() {
        initWeb();
        initBottom();
        brief_url = getResources().getStringArray(R.array.brief_url);
    }

    public static Intent newIntent(Context context, String url) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Constant.NEW_URL, url);
        return intent;
    }

    private void initWeb() {
        String new_url = getIntent().getStringExtra(Constant.NEW_URL);
        Log.d(TAG, "initWeb: "+new_url);

        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setJavaScriptEnabled(true);//加载JavaScript
        webView.setWebViewClient(mWebViewClient);//这个一定要设置，要不然不会再本应用中加载
        webView.setWebChromeClient(mWebChromeClient);
        webView.loadUrl(new_url);
    }

    /**
     * 底部栏
     */
    private void initBottom() {
        tv_index.setTextColor(getResources().getColor(R.color.txt_2));
        Drawable img = tv_index.getResources().getDrawable(R.drawable.index_on);
        // 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
        img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
        tv_index.setCompoundDrawables(null, img, null, null); //设置左图标
    }

    @OnClick({R.id.tv_sum, R.id.tv_index, R.id.tv_map, R.id.tv_center})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_index:
                //startActivity(WebActivity.newIntent(this, "http://121.15.203.82:9210/WAN_MPDA_Pic/PageMain/ProjectList.aspx"));
                ToastUtil.show(getApplicationContext(), "当前为首页");
                break;

            case R.id.tv_map:
                showPopupProject();
                break;

            case R.id.tv_sum:
                showPopupSum();
                break;
            case R.id.tv_center:
                showPopupCenter();
                break;

        }
    }

    /**
     * 弹出框  项目
     */
    private void showPopupProject() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.pop_project, null);
        final PopupWindow pop = new PopupWindow(contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        pop.setContentView(contentView);
        pop.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        pop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setOutsideTouchable(true);
        pop.setAnimationStyle(R.anim.mypop_anim);
        pop.showAsDropDown(tv_map, Gravity.TOP, 10);
        //通讯录
        contentView.findViewById(R.id.tv_contact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TwoLogin(brief_url[12]);
                pop.dismiss();
            }
        });
        //项目信息
        contentView.findViewById(R.id.tv_inft).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TwoLogin(brief_url[0]);
                pop.dismiss();
            }
        });
        //项目文件信息
        contentView.findViewById(R.id.tv_doc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TwoLogin(brief_url[14]);
                pop.dismiss();
            }
        });
        //项目照片信息
        contentView.findViewById(R.id.tv_picinfo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TwoLogin(brief_url[15]);
                pop.dismiss();
            }
        });
    }

    /**
     * 弹出框
     */
    private void showPopupCenter() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.pop_center, null);
        final PopupWindow pop = new PopupWindow(contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        pop.setContentView(contentView);
        pop.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        pop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setOutsideTouchable(true);
        pop.setAnimationStyle(R.anim.mypop_anim);
        pop.showAsDropDown(tv_center, Gravity.TOP, 10);
        //内部资料
        contentView.findViewById(R.id.tv_reference_private).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: "+brief_url[1]);
                TwoLogin(brief_url[1]);
                pop.dismiss();
            }
        });
        //图书期刊
        contentView.findViewById(R.id.tv_book).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: "+brief_url[2]);
                TwoLogin(brief_url[2]);
                pop.dismiss();
            }
        });
        //常用表格
        contentView.findViewById(R.id.tv_table).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TwoLogin(brief_url[3]);
                pop.dismiss();
            }
        });
        //单位规章
        contentView.findViewById(R.id.tv_unit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TwoLogin(brief_url[4]);
                pop.dismiss();
            }
        });
        //标准规范
        contentView.findViewById(R.id.tv_standard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TwoLogin(brief_url[5]);
                pop.dismiss();
            }
        });
    }

    /**
     * 弹出框
     */
    private void showPopupSum() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.pop_sum, null);
        final PopupWindow pop = new PopupWindow(contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        pop.setContentView(contentView);
        pop.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        pop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setOutsideTouchable(true);
        pop.setAnimationStyle(R.anim.mypop_anim);
        pop.showAsDropDown(tv_sum, Gravity.TOP, 10);
        //按项目状态
        contentView.findViewById(R.id.tv_pro_stat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TwoLogin(brief_url[6]);
                pop.dismiss();
            }
        });
        //项目科室
        contentView.findViewById(R.id.tv_department).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TwoLogin(brief_url[7]);
                pop.dismiss();
            }
        });
        //项目类别
        contentView.findViewById(R.id.tv_type).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TwoLogin(brief_url[8]);
                pop.dismiss();
            }
        });
        //项目投资
        contentView.findViewById(R.id.tv_tze).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TwoLogin(brief_url[9]);
                pop.dismiss();
            }
        });
        //汇总统计
        contentView.findViewById(R.id.tv_reference).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TwoLogin(brief_url[10]);
                pop.dismiss();
            }
        });
        //品牌公开
        contentView.findViewById(R.id.tv_brand).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TwoLogin(brief_url[11]);
                pop.dismiss();
            }
        });

    }


    /**
     * 第二次验证登录
     * <p>
     * 验证服务器是否变更
     * @param s
     */
    private void TwoLogin(final String s) {
        MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/xml; charset=utf-8");//根据C#大牛那边写的头文件 以及登录验证方式
        OkHttpClient okHttpClient = new OkHttpClient();
        String strXML = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>" +
                "<REQUEST>" +
                "<SP_ID>ToWanPic</SP_ID>" +
                "<PASSWORD>" + "vJo06/qsLDOK5p2FvLqujo8G9eCsjrLJGcg8TGN0QZexSchZjBfneZ1vL4h3BN/EEId5hEBxZWM=" + "</PASSWORD>" +
                "<USER>yzdw-gly</USER>" +
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
                Log.d(TAG, "onResponse: 第二次登录成功" + string);
                try {
                    parseXMLWithPull(string,s);//对xml的解析
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * xml的pull解析
     *
     * @param xmlData
     * @throws Exception
     */
    public void parseXMLWithPull(String xmlData,String s) throws Exception {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(new StringReader(xmlData));
        int eventType = parser.getEventType();
        String resp_code = "";
        String resp_desc = "";
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
                            startActivity(WebActivity.newIntent(getApplicationContext(), Constant.iniUrl + websession + "&OpenType=" + s));
                            dialog.dismiss();
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


    //ChromeClient   监听网页加载
    WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
            result.confirm();
            return true;
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
        }
    };

    //WebViewClient
    WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // 消耗掉这个事件。Android中返回True的即到此为止吧,事件就会不会冒泡传递了，我们称之为消耗掉
            // 使用自己的WebView组件来响应Url加载事件，而不是使用默认浏览器器加载页面
            webView.loadUrl(url);
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
        }
    };

    private long firstTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()){
            webView.goBack();
        }else if (keyCode == KeyEvent.KEYCODE_BACK){
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000){
                ToastUtil.show(getApplicationContext(),"再按一次退出程序");
                firstTime = secondTime;
            }else {
                finish();
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null){
            webView.destroy();
        }
    }
}
