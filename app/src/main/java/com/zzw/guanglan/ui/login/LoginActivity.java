package com.zzw.guanglan.ui.login;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zzw.guanglan.BuildConfig;
import com.zzw.guanglan.R;
import com.zzw.guanglan.base.BaseActivity;
import com.zzw.guanglan.bean.LoginBean;
import com.zzw.guanglan.bean.UserBean;
import com.zzw.guanglan.http.Api;
import com.zzw.guanglan.http.retrofit.RetrofitHttpEngine;
import com.zzw.guanglan.manager.UserManager;
import com.zzw.guanglan.rx.ErrorObserver;
import com.zzw.guanglan.rx.LifeObservableTransformer;
import com.zzw.guanglan.ui.ConfigIpActivity;
import com.zzw.guanglan.ui.resource.ResourceActivity;
import com.zzw.guanglan.utils.RequestBodyUtils;
import com.zzw.guanglan.utils.SPUtil;
import com.zzw.guanglan.utils.ToastUtils;
import com.zzw.guanglan.widgets.MultiFunctionEditText;

import java.util.HashMap;

import butterknife.BindView;
import io.reactivex.functions.Consumer;

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
            etPhone.setText("njtest");
            etPwd.setText("njtest");
        }

        LoginBean bean = SPUtil.getInstance().getSerializable("lastLogin", null);
        if (bean != null) {
            etPhone.setText(bean.getStaffNbr());
            etPwd.setText(bean.getPassword());
        }

        per();
    }

    private void per() {
        new RxPermissions(this)
                .request(new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                }).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (!aBoolean) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setTitle("温馨提示");
                    builder.setCancelable(false);
                    builder.setMessage("为了您的正常使用,请开启必要的权限");
                    builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            per();
                        }
                    });
                    builder.create().show();
                }
            }
        });
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
                .compose(LifeObservableTransformer.<UserBean>create(this))
                .subscribe(new ErrorObserver<UserBean>(this) {
                    @Override
                    public void onNext(UserBean bean) {
                        if (bean.getCode() == 0) {
                            LoginBean bean1 = new LoginBean();
                            bean1.setStaffNbr(staffNbr);
                            bean1.setPassword(password);
                            SPUtil.getInstance().put("lastLogin", bean1);

                            bean.setStaffNbr(staffNbr);
                            UserManager.getInstance().setUserBean(bean);
                            finish();
                            ResourceActivity.open(LoginActivity.this);
//                            MainActivity.open(LoginActivity.this);
                        }else{
                            ToastUtils.showToast(TextUtils.isEmpty(bean.getMsg())?"未知错误":bean.getMsg());
                        }
                    }
                });
    }


    @Override
    protected boolean backable() {
        return false;
    }

}
