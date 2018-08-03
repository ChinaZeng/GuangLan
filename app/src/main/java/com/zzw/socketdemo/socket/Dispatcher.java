package com.zzw.socketdemo.socket;

import android.support.annotation.NonNull;

import java.io.OutputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Dispatcher {

    private static Dispatcher instance;

    private Dispatcher() {
    }

    public static Dispatcher getInstance() {
        return SingletonHolder.mInstance;
    }

    private static class SingletonHolder {
        private static volatile Dispatcher mInstance = new Dispatcher();
    }

     ThreadPoolExecutor executor = new ThreadPoolExecutor(5, Integer.MAX_VALUE,
            10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(5), new ThreadFactory() {
        @Override
        public Thread newThread(@NonNull Runnable r) {
            return new Thread(r);
        }
    });




}
