package com.zhou.wanve2.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.zhou.wanve2.R;
import com.zhou.wanve2.base.BaseActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import butterknife.BindView;
import butterknife.OnClick;

public class StartActivity extends BaseActivity {

    private static final String TAG = "StartActivity";

    @BindView(R.id.tv_next) TextView tv_bext;

    @Override
    public int getLayout() {
        return R.layout.activity_start;
    }

    @Override
    public void init() {

    }

    @OnClick({R.id.tv_next}) void onClick(View view){
        switch (view.getId()){
            case R.id.tv_next:
                SharedPreferences sp = getSharedPreferences("config", Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = sp.edit();
                edit.putBoolean("FIRST",true);
                edit.commit();
                startToActivity(LoginActivity.class);
                break;
        }
    }
}
