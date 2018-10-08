package com.zzw.guanglan;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.zzw.guanglan.socket.utils.ByteUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.WRITE_SETTINGS,}, 5);


    }

    public void compile(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String src = "/storage/emulated/0/ocr/card.jpg";
                String dst = "/storage/emulated/0/ocr/cardn.jpg";
                String text = "/storage/emulated/0/ocr/c1.text";
                String text2 = "/storage/emulated/0/ocr/c2.text";

                FileInputStream srcFis = null;
                FileInputStream dstFis = null;
                FileOutputStream srcFos = null;
                FileOutputStream dstFos = null;
                try {
                    srcFis = new FileInputStream(new File(src));
                    dstFis = new FileInputStream(new File(dst));
                    srcFos = new FileOutputStream(new File(text));
                    dstFos = new FileOutputStream(new File(text2));
                    if (srcFis.available() == dstFis.available()) {
                        Log.e("zzz", "长度相同:" + srcFis.available());
                    }

                    byte[] buffer1 = new byte[2048];
                    byte[] buffer2 = new byte[2048];
                    int len = 0;
                    Log.e("zzz", "------------------------------");
                    while ((len = srcFis.read(buffer1, 0, buffer1.length)) > 0) {
                        byte[] data = buffer1;
                        if (len < buffer1.length) {
                            data = ByteUtil.subBytes(buffer1, 0, len);
                        }
                        srcFos.write((Arrays.toString(data)+"\n").getBytes("UTF-8"));
                    }
                    srcFos.flush();

                    while ((len = dstFis.read(buffer2, 0, buffer2.length)) > 0) {
                        byte[] data = buffer2;
                        if (len < buffer2.length) {
                            data = ByteUtil.subBytes(buffer2, 0, len);
                        }
                        dstFos.write((Arrays.toString(data)+"\n").getBytes("UTF-8"));
                    }
                    dstFos.flush();
                    Log.e("zzz", "------------------------------");
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void server(View view) {
        startActivity(new Intent(this, ServerActivity.class));
    }

    public void client(View view) {
        startActivity(new Intent(this, ClientActivity.class));
    }
}
