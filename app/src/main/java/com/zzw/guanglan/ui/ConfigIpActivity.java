package com.zzw.guanglan.ui;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.zzw.guanglan.R;
import com.zzw.guanglan.base.BaseActivity;
import com.zzw.guanglan.http.retrofit.RetrofitHttpEngine;
import com.zzw.guanglan.utils.ToastUtils;
import com.zzw.guanglan.widgets.MultiFunctionEditText;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.logging.HttpLoggingInterceptor;
import timber.log.Timber;

public class ConfigIpActivity extends BaseActivity {
    @BindView(R.id.et_ip)
    MultiFunctionEditText etIp;
    @BindView(R.id.et_port)
    MultiFunctionEditText etPort;

    public static void open(Context context) {
        context.startActivity(new Intent(context, ConfigIpActivity.class));
    }

    @Override
    protected int initLayoutId() {
        return R.layout.activity_config_ip;
    }

    @Override
    protected void initView() {
        super.initView();

        HttpUrl httpUrl = RetrofitHttpEngine.getBaseUrl();
        if (httpUrl != null) {
            etIp.setText(httpUrl.scheme() + "://" + httpUrl.host());
            etPort.setText(httpUrl.port() + "");
        }
    }

    @OnClick(R.id.bt_sure)
    public void onViewClicked() {
        String ip = etIp.getText().toString().trim();
        String port = etPort.getText().toString().trim();
        if (TextUtils.isEmpty(ip) || TextUtils.isEmpty(port)) {
            ToastUtils.showToast("请输入ip和端口号");
            return;
        }

        if (!ip.startsWith("http://") && !ip.startsWith("https://")) {
            ToastUtils.showToast("ip开始请加上http://或者http://");
            return;
        }


        RetrofitHttpEngine.builder()
                .baseUrl(ip + ":" + port)
                .interceptors(new Interceptor[]{
                        new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                            @Override
                            public void log(String message) {
                                Timber.tag("okhttp").w(message);
                            }
                        })
                                .setLevel(HttpLoggingInterceptor.Level.BODY)
                })
                .build();
        ToastUtils.showToast("修改成功");
        finish();
    }
}
