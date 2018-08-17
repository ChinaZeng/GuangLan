package com.zzw.socketdemo.http.retrofit.error;

import android.app.Activity;
import android.support.annotation.MainThread;

public interface IExceptionHandler {
    @MainThread
    boolean handle(final Activity activity, Throwable t);
}
