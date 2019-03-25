package com.zzw.guanglan.http.retrofit.error;

import android.app.Activity;
import android.net.ParseException;
import android.text.TextUtils;

import com.google.gson.JsonParseException;
import com.zzw.guanglan.rx.CodeException;
import com.zzw.guanglan.utils.ToastUtils;

import org.json.JSONException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.HttpException;

public class DefaultExceptionHandler implements IExceptionHandler {
    @Override
    public boolean handle(Activity activity, Throwable e) {
        e.printStackTrace();
        String msg = "未知错误";
        if (e instanceof UnknownHostException || e instanceof ConnectException) {
            msg = "网络不可用";
        } else if (e instanceof SocketTimeoutException) {
            msg = "请求网络超时";
        }else if (e instanceof CodeException) {
            String msg1 = ((CodeException) e).getMsg();
            if(TextUtils.isEmpty(msg1)){
                msg = msg1;
            }
        } else if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            msg = convertStatusCode(httpException);
        } else if (e instanceof JsonParseException || e instanceof ParseException || e instanceof JSONException) {
            msg = "数据解析错误";
        }
        ToastUtils.showToast(msg);

        return false;
    }

    private String convertStatusCode(HttpException httpException) {
        String msg;
        if (httpException.code() == 500) {
            msg = "服务器发生错误";
        } else if (httpException.code() == 404) {
            msg = "请求地址不存在";
        } else if (httpException.code() == 403) {
            msg = "请求被服务器拒绝";
        } else if (httpException.code() == 307) {
            msg = "请求被重定向其他页面";
        } else {
            msg = httpException.message();
        }
        return msg;
    }


}
