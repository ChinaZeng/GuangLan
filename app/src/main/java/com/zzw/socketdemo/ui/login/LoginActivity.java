package com.zzw.socketdemo.ui.login;

import android.view.View;

import com.zzw.socketdemo.R;
import com.zzw.socketdemo.base.BaseActivity;
import com.zzw.socketdemo.http.Api;
import com.zzw.socketdemo.http.retrofit.RetrofitHttpEngine;
import com.zzw.socketdemo.rx.ErrorObserver;
import com.zzw.socketdemo.rx.LifeObservableTransformer;
import com.zzw.socketdemo.rx.ResultBooleanFunction;
import com.zzw.socketdemo.ui.MainActivity;
import com.zzw.socketdemo.utils.RequestBodyUtils;

import java.util.HashMap;

public class LoginActivity extends BaseActivity {

    @Override
    protected void initView() {
        super.initView();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            // 判断是否有WRITE_SETTINGS权限if(!Settings.System.canWrite(this))
//            if (!Settings.System.canWrite(this)) {
//                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
//                        Uri.parse("package:" + getPackageName()));
//                startActivityForResult(intent, 1);
//            }
//        }
    }

    @Override
    protected int initLayoutId() {
        return R.layout.activity_login;
    }

    public void login(View view) {
        RetrofitHttpEngine.obtainRetrofitService(Api.class)
                .login(RequestBodyUtils.generateRequestBody(new HashMap<String, String>() {
                    {
                        put("staffNbr", "ADMIN");
                        put("password", "123456");
                    }
                }))
                .map(ResultBooleanFunction.create())
                .compose(LifeObservableTransformer.<Boolean>create(this))
                .subscribe(new ErrorObserver<Boolean>(this) {
                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            MainActivity.open(LoginActivity.this);
                        }
                    }
                });
    }
}
