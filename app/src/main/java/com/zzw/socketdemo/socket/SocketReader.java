package com.zzw.socketdemo.socket;

import android.annotation.SuppressLint;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.regex.Pattern;

public class SocketReader {
    private static final String TAG = "SocketReader";

    private final static int BUFFER_SIZE = 4096;
    private final static int HEADER_BUFFER_SIZE = 4;

    public Packet readData(Socket socket,InputStream inputStream) {
        //读取数据 封装packet
        try {
            Packet packet = new Packet(socket, Packet.TYPE.RECIVER);
            int bodyLength = getBodyLength(inputStream);
            if(bodyLength<=0){
                return null;
            }
            byte[] bodyBuffer = readIs2(inputStream, bodyLength);
            packet.cmd = bodyBuffer[0];
            packet.flog = bodyBuffer[1];
            packet.data = ByteUtils.subBytes(bodyBuffer,2,bodyBuffer.length-2);
            return packet;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private int getBodyLength(InputStream is) throws IOException {
        byte[] b = readIs2(is, HEADER_BUFFER_SIZE);
        int len = ByteUtils.getInt(b);
        MyLog.e(TAG, "读取到包头：" + len);
        return len;
    }


    private  byte[] readIs2(InputStream is, int len) throws IOException {
        MyLog.e(TAG,String.format("开始读取输入流，InputStream的长度：%d，要读取的长度为：%d", is.available(), len));

        byte[] data = new byte[len];
        int i = 0;
        while (i < len) {
            int r = is.read(data, i, len - i);
            if (r <= 0)
                return data;

            i = i + r;
        }
        MyLog.e(TAG,"---------------读取结束---------------");
        return data;
    }


    @SuppressLint("DefaultLocale")
    private byte[] readIs(InputStream is, int len) throws IOException {
        MyLog.e(TAG,String.format("开始读取输入流，InputStream的长度：%d，要读取的长度为：%d", is.available(), len));

        int tempBuffSize = len;
        if (len > BUFFER_SIZE) {
            tempBuffSize = BUFFER_SIZE;
        }

        byte[] data = new byte[len];

        int readLength = 0;
        if (len > BUFFER_SIZE) {// 判断是否需要分批读取
            int ret = len % tempBuffSize;// 根据余数来判断是否除尽
            int readTimes = len / tempBuffSize;
            if (ret > 0) {// 如果余数大余0，则需要多读一次
                readTimes++;
            }

            MyLog.e("---------------------------------分隔符---------------------------------");

            byte[] tempBuff = null;
            for (int i = 0; i < readTimes; i++) {
                if (i == readTimes - 1) {// 判断是否最后一段
                    tempBuff = new byte[ret];
                } else {
                    tempBuff = new byte[tempBuffSize];
                }
                int readLen = is.read(tempBuff, 0, tempBuffSize);
                System.arraycopy(tempBuff,0,data,readLength,readLen);
                readLength += tempBuff.length;
                MyLog.e(String.format("第%d次读取数据，长度:%d", i, readLen));
            }
        } else {
            readLength = is.read(data, 0, len);
        }
        if(readLength<len){
            System.arraycopy(data, 0, data, 0, readLength);
        }
        MyLog.e(TAG,String.format("本次需要读取长度%d,实际读取到的长度%d",len, readLength));
        return data;
    }

}
