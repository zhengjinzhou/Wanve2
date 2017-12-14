package com.zhou.wanve2.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.zhou.wanve2.R;
import com.zhou.wanve2.base.BaseActivity;
import com.zhou.wanve2.base.Constant;
import com.zhou.wanve2.util.ToastUtil;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    @BindView(R.id.tv_index)
    TextView tv_index;
    @BindView(R.id.tv_map)
    TextView tv_map;
    @BindView(R.id.tv_sum)
    TextView tv_sum;
    @BindView(R.id.tv_center)
    TextView tv_center;
    private String websession;

    public static Intent newIntent(Context context, String websession) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Constant.WEBSESSION, websession);
        return intent;
    }

    @Override
    public int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void init() {

        initBottom();
        websession = getIntent().getStringExtra(Constant.WEBSESSION);//获取websession

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
                startActivity(WebActivity.newIntent(this, Constant.iniUrl + websession + "&OpenType=" + Constant.zszx_nb));
                //startToActivity(WebActivity.class);
                ToastUtil.show(getApplicationContext(), "首页");
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
     * 弹出框
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
        contentView.findViewById(R.id.tv_data).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //WebActivity.newIntent(getApplicationContext(),Constant.iniUrl + websession + Constant.zszx_nb);
                pop.dismiss();
            }
        });

        contentView.findViewById(R.id.tv_standard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.show(getApplicationContext(), "待续");
                pop.dismiss();
            }
        });

        contentView.findViewById(R.id.tv_table).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.show(getApplicationContext(), "待续");
                pop.dismiss();
            }
        });

        contentView.findViewById(R.id.tv_rules).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.show(getApplicationContext(), "待续");
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
        contentView.findViewById(R.id.tv_table).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.show(getApplicationContext(), "待续");
                pop.dismiss();
            }
        });
        contentView.findViewById(R.id.tv_distribution).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.show(getApplicationContext(), "待续");
                pop.dismiss();
            }
        });
        contentView.findViewById(R.id.tv_project).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.show(getApplicationContext(), "待续");
                pop.dismiss();
            }
        });
    }
}
