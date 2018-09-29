package com.zzw.socketdemo.ui.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.View;

import com.zzw.socketdemo.R;
import com.zzw.socketdemo.base.BaseActivity;
import com.zzw.socketdemo.http.Api;
import com.zzw.socketdemo.http.retrofit.RetrofitHttpEngine;
import com.zzw.socketdemo.rx.ErrorObserver;
import com.zzw.socketdemo.ui.MainActivity;

import java.util.HashMap;

public class LoginActivity extends BaseActivity {

    @Override
    protected void initView() {
        super.initView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 判断是否有WRITE_SETTINGS权限if(!Settings.System.canWrite(this))
            if (!Settings.System.canWrite(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 1);
            }
        }
    }

    @Override
    protected int initLayoutId() {
        return R.layout.activity_login;
    }

    public void login(View view) {
        RetrofitHttpEngine.obtainRetrofitService(Api.class)
                .login(new HashMap<String, String>() {
                    {
                        put("staffNbr", "ADMIN");
                        put("password", "123456");
                    }
                })
                .compose(transformer())
                .subscribe(new ErrorObserver<Object>(this) {
                    @Override
                    public void onNext(Object o) {
                        MainActivity.open(LoginActivity.this);
                    }
                });

    }
}
