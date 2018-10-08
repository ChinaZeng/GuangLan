package com.zzw.socketdemo.ui.login;

import android.view.View;

import com.zzw.socketdemo.R;
import com.zzw.socketdemo.base.BaseActivity;
import com.zzw.socketdemo.bean.LoginResultBean;
import com.zzw.socketdemo.http.Api;
import com.zzw.socketdemo.http.retrofit.RetrofitHttpEngine;
import com.zzw.socketdemo.manager.UserManager;
import com.zzw.socketdemo.rx.ErrorObserver;
import com.zzw.socketdemo.rx.LifeObservableTransformer;
import com.zzw.socketdemo.rx.ResultBooleanFunction;
import com.zzw.socketdemo.ui.MainActivity;
import com.zzw.socketdemo.utils.RequestBodyUtils;
import com.zzw.socketdemo.utils.SPUtil;
import com.zzw.socketdemo.utils.ToastUtils;
import com.zzw.socketdemo.widgets.MultiFunctionEditText;

import java.util.HashMap;

import butterknife.BindView;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.et_phone)
    MultiFunctionEditText etPhone;
    @BindView(R.id.et_pwd)
    MultiFunctionEditText etPwd;

    @Override
    protected void initView() {
        super.initView();
        setTitle("登录");
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

        if (etPhone.getText().toString().trim().length() == 0) {
            ToastUtils.showToast("请输入用户名");
            return;
        }
        if (etPwd.getText().toString().trim().length() == 0) {
            ToastUtils.showToast("请输入密码");
            return;
        }


        RetrofitHttpEngine.obtainRetrofitService(Api.class)
                .login(RequestBodyUtils.generateRequestBody(new HashMap<String, String>() {
                    {
                        put("staffNbr", etPhone.getText().toString().trim());
                        put("password", etPwd.getText().toString().trim());
                    }
                }))
                .compose(LifeObservableTransformer.<LoginResultBean>create(this))
                .subscribe(new ErrorObserver<LoginResultBean>(this) {
                    @Override
                    public void onNext(LoginResultBean bean) {
                        if (bean.getCode() == 0) {
                            UserManager.getInstance().setUserId(bean.getUserId());
                            finish();
                            MainActivity.open(LoginActivity.this);
                        }
                    }
                });
    }


    @Override
    protected boolean backable() {
        return false;
    }

}
