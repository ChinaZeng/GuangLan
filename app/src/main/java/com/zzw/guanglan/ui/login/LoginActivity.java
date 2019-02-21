package com.zzw.guanglan.ui.login;

import android.view.View;

import com.zzw.guanglan.BuildConfig;
import com.zzw.guanglan.R;
import com.zzw.guanglan.base.BaseActivity;
import com.zzw.guanglan.bean.LoginBean;
import com.zzw.guanglan.bean.LoginResultBean;
import com.zzw.guanglan.http.Api;
import com.zzw.guanglan.http.retrofit.RetrofitHttpEngine;
import com.zzw.guanglan.manager.UserManager;
import com.zzw.guanglan.rx.ErrorObserver;
import com.zzw.guanglan.rx.LifeObservableTransformer;
import com.zzw.guanglan.ui.ConfigIpActivity;
import com.zzw.guanglan.ui.MainActivity;
import com.zzw.guanglan.ui.resource.ResourceActivity;
import com.zzw.guanglan.utils.RequestBodyUtils;
import com.zzw.guanglan.utils.SPUtil;
import com.zzw.guanglan.utils.ToastUtils;
import com.zzw.guanglan.widgets.MultiFunctionEditText;

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

        if (BuildConfig.DEBUG) {
            etPhone.setText("ADMIN");
            etPwd.setText("njtest");
        }

        LoginBean bean = SPUtil.getInstance().getSerializable("lastLogin", null);
        if (bean != null) {
            etPhone.setText(bean.getStaffNbr());
            etPwd.setText(bean.getPassword());
        }
    }

    @Override
    protected int initLayoutId() {
        return R.layout.activity_login;
    }

    private int clickCount;
    private long lastClickTime;

    public void logo(View view) {

        long nowTime = System.currentTimeMillis();
        if (nowTime - lastClickTime > 200) {
            clickCount = 1;
        } else {
            clickCount++;
        }
        lastClickTime = nowTime;

        if (clickCount > 5) {
            ConfigIpActivity.open(this);
        }
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

        final String staffNbr = etPhone.getText().toString().trim();
        final String password = etPwd.getText().toString().trim();
        RetrofitHttpEngine.obtainRetrofitService(Api.class)
                .login(RequestBodyUtils.generateRequestBody(new HashMap<String, String>() {
                    {
                        put("staffNbr", staffNbr);
                        put("password", password);
                    }
                }))
                .compose(LifeObservableTransformer.<LoginResultBean>create(this))
                .subscribe(new ErrorObserver<LoginResultBean>(this) {
                    @Override
                    public void onNext(LoginResultBean bean) {
                        if (bean.getCode() == 0) {
                            LoginBean bean1 = new LoginBean();
                            bean1.setStaffNbr(staffNbr);
                            bean1.setPassword(password);
                            SPUtil.getInstance().put("lastLogin", bean1);

                            UserManager.getInstance().setUserId(bean.getUserId());
                            finish();
                            ResourceActivity.open(LoginActivity.this);
//                            MainActivity.open(LoginActivity.this);
                        }
                    }
                });
    }


    @Override
    protected boolean backable() {
        return false;
    }

}
