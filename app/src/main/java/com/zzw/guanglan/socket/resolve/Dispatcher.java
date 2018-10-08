package com.zzw.guanglan.socket.resolve;

import android.support.annotation.NonNull;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Dispatcher {

    private ThreadPoolExecutor executor;

    public Dispatcher() {
        executor = new ThreadPoolExecutor(5, Integer.MAX_VALUE,
                10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(5), new ThreadFactory() {
            @Override
            public Thread newThread(@NonNull Runnable r) {
                return new Thread(r);
            }
        });
    }

//    public static Dispatcher getInstance() {
//        return SingletonHolder.mInstance;
//    }
//
//    private static class SingletonHolder {
//        private static volatile Dispatcher mInstance = new Dispatcher();
//    }


    public void shutdownNow(){
        executor.shutdownNow();
        executor = null;
    }


    public void submit(Runnable runnable) {
        executor.submit(runnable);
    }


}
