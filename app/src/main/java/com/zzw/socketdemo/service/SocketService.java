package com.zzw.socketdemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.zzw.socketdemo.socket.CMD;
import com.zzw.socketdemo.socket.EventBusTag;
import com.zzw.socketdemo.socket.event.ConnBean;
import com.zzw.socketdemo.socket.event.GetSorFileBean;
import com.zzw.socketdemo.socket.event.ReBean;
import com.zzw.socketdemo.socket.event.TestArgsAndStartBean;
import com.zzw.socketdemo.socket.listener.STATUS;
import com.zzw.socketdemo.socket.listener.StatusListener;
import com.zzw.socketdemo.socket.manager.ServerManager;
import com.zzw.socketdemo.socket.resolve.Packet;
import com.zzw.socketdemo.socket.utils.ByteUtil;
import com.zzw.socketdemo.socket.utils.MyLog;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.Arrays;

public class SocketService extends Service implements StatusListener {

    private ServerManager serverManager;

    private final int PORT = 8825;
    private String key;

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        serverManager = new ServerManager(PORT);
        serverManager.setListener(this);
        serverManager.startServer();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        serverManager.close();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void statusChange(String key, STATUS status) {

        if (status == STATUS.END) {
            String content = key + "断开连接";
            MyLog.e(content);
            this.key = null;
        } else if (status == STATUS.INIT) {
            String content = key + "初始化接收线程";
            MyLog.e(content);
        } else if (status == STATUS.RUNNING) {
            String content = key + "建立连接,开始运行";
            MyLog.e(content);
            this.key = key;
        }

        ConnBean event = new ConnBean();
        event.status = status;
        event.key = key;
        EventBus.getDefault().post(event);
    }


    @Subscriber(tag = EventBusTag.GET_DEVICE_SERIAL_NUMBER)
    public void getDeviceSerialNumber(int cmd) {
        if (key != null) {
            serverManager.getDeviceSerialNumber(key);
        }
    }


    @Subscriber(tag = EventBusTag.SEND_TEST_ARGS_AND_START_TEST)
    public void sendTestArgsAndStartTest(TestArgsAndStartBean bean) {
        if (key != null) {
            serverManager.sendTestArgsAndStartTestPacket(key, bean);
        }
    }

    @Subscriber(tag = EventBusTag.GET_SOR_FILE)
    public void getSorFile(GetSorFileBean bean) {
        if (key != null) {
            serverManager.getSorFile(key, bean);
        }
    }

    @Subscriber(tag = EventBusTag.SEND_RE)
    public void getSorFile(ReBean bean) {
        if (key != null) {
            serverManager.sendRe(key, bean);
        }
    }

    @Subscriber(tag = EventBusTag.SEND_HEART)
    public void sendHeart(int flog) {
        if (key != null) {
            serverManager.sendHeart(key);
        }
    }

    @Subscriber(tag = EventBusTag.RE_HEART)
    public void reHeart(int flog) {
        if (key != null) {
            serverManager.reHeart(key);
        }
    }

    @Subscriber(tag = EventBusTag.TAG_RECIVE_MSG)
    public void reciverMsg(Packet packet) {
        StringBuilder builder = new StringBuilder();
        builder.append("起始值:" + Arrays.toString(ByteUtil.intToBytes(Packet.START_FRAME)) + "\n");
        builder.append("总帧长度:" + Arrays.toString(ByteUtil.intToBytes(packet.pkAllLen)) + "\n");
        builder.append("版本号:" + Arrays.toString(ByteUtil.intToBytes(packet.rev)) + "\n");
        builder.append("源地址:" + Arrays.toString(ByteUtil.intToBytes(packet.src)) + "\n");
        builder.append("目标地址:" + Arrays.toString(ByteUtil.intToBytes(packet.dst)) + "\n");
        builder.append("帧类型:" + Arrays.toString(ByteUtil.shortToBytes(packet.pkType)) + "\n");
        builder.append("流水号:" + Arrays.toString(ByteUtil.shortToBytes((short) packet.pktId)) + "\n");
        builder.append("保留字节:" + Arrays.toString(ByteUtil.intToBytes(packet.keep)) + "\n");
        builder.append("cmd:" + Arrays.toString(ByteUtil.intToBytes(packet.cmd)) + "\n");
        builder.append("数据长度:" + Arrays.toString(ByteUtil.intToBytes(packet.cmdDataLength)) + "\n");
        builder.append("数据:" + Arrays.toString(packet.data) + "\n");
        builder.append("结尾值:" + Arrays.toString(ByteUtil.intToBytes(Packet.END_FRAME)) + "\n");
        MyLog.e(builder.toString());
    }

    @Subscriber(tag = EventBusTag.TAG_SEND_MSG)
    public void sendMsg(Packet packet) {
        StringBuilder builder = new StringBuilder();
        if (packet.cmd == CMD.GET_DEVICE_SERIAL_NUMBER) {
            builder.append("发送获取设备号命令成功\n");
        }
        builder.append("起始值:" + Arrays.toString(ByteUtil.intToBytes(Packet.START_FRAME)) + "\n");
        builder.append("总帧长度:" + Arrays.toString(ByteUtil.intToBytes(packet.pkAllLen)) + "\n");
        builder.append("版本号:" + Arrays.toString(ByteUtil.intToBytes(packet.rev)) + "\n");
        builder.append("源地址:" + Arrays.toString(ByteUtil.intToBytes(packet.src)) + "\n");
        builder.append("目标地址:" + Arrays.toString(ByteUtil.intToBytes(packet.dst)) + "\n");
        builder.append("帧类型:" + Arrays.toString(ByteUtil.shortToBytes(packet.pkType)) + "\n");
        builder.append("流水号:" + Arrays.toString(ByteUtil.shortToBytes((short) packet.pktId)) + "\n");
        builder.append("保留字节:" + Arrays.toString(ByteUtil.intToBytes(packet.keep)) + "\n");
        builder.append("cmd:" + Arrays.toString(ByteUtil.intToBytes(packet.cmd)) + "\n");
        builder.append("数据长度:" + Arrays.toString(ByteUtil.intToBytes(packet.cmdDataLength)) + "\n");
        builder.append("数据:" + Arrays.toString(packet.data) + "\n");
        builder.append("结尾值:" + Arrays.toString(ByteUtil.intToBytes(Packet.END_FRAME)) + "\n");
        MyLog.e(builder.toString());
    }


}
