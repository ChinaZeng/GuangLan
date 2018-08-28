package com.zzw.socketdemo.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

public class ToastUtils {

    private static Toast mToast;

    public static void init(Context context) {
        mToast = Toast.makeText(context.getApplicationContext(), "", Toast.LENGTH_SHORT);
    }


    public static void showToast(String text) {
        if (TextUtils.isEmpty(text))
            return;
        if (mToast == null) {
            return;
        }
        mToast.setText(text);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.show();
    }
}
