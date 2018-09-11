package com.zzw.socketdemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.zzw.socketdemo.socket.CMD;
import com.zzw.socketdemo.socket.EventBusTag;
import com.zzw.socketdemo.socket.event.ConnBean;
import com.zzw.socketdemo.socket.event.SorFileBean;
import com.zzw.socketdemo.socket.event.ReBean;
import com.zzw.socketdemo.socket.event.TestArgsAndStartBean;
import com.zzw.socketdemo.socket.listener.STATUS;
import com.zzw.socketdemo.socket.listener.StatusListener;
import com.zzw.socketdemo.socket.manager.ServerManager;
import com.zzw.socketdemo.socket.resolve.Packet;
import com.zzw.socketdemo.socket.utils.ByteUtil;
import com.zzw.socketdemo.socket.utils.FileHelper;
import com.zzw.socketdemo.socket.utils.MyLog;
import com.zzw.socketdemo.utils.MD5Utils;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

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


    @Subscriber(tag = EventBusTag.SEND_TEST_ARGS_AND_STOP_TEST)
    public void sendTestArgsAndStopTest(int flog) {
        if (key != null) {
            serverManager.sendTestArgsAndStopTestPacket(key);
        }
    }


    @Subscriber(tag = EventBusTag.SEND_TEST_ARGS_AND_START_TEST)
    public void sendTestArgsAndStartTest(TestArgsAndStartBean bean) {
        if (key != null) {
            serverManager.sendTestArgsAndStartTestPacket(key, bean);
        }
    }

    @Subscriber(tag = EventBusTag.GET_SOR_FILE)
    public void getSorFile(SorFileBean bean) {
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
        builder.append("起始值:" + ByteUtil.bytesToHexSpaceString(ByteUtil.intToBytes(Packet.START_FRAME)) + "\n");
        builder.append("总帧长度:" + ByteUtil.bytesToHexSpaceString(ByteUtil.intToBytes(packet.pkAllLen)) + "\n");
        builder.append("版本号:" + ByteUtil.bytesToHexSpaceString(ByteUtil.intToBytes(packet.rev)) + "\n");
        builder.append("源地址:" + ByteUtil.bytesToHexSpaceString(ByteUtil.intToBytes(packet.src)) + "\n");
        builder.append("目标地址:" + ByteUtil.bytesToHexSpaceString(ByteUtil.intToBytes(packet.dst)) + "\n");
        builder.append("帧类型:" + ByteUtil.bytesToHexSpaceString(ByteUtil.shortToBytes(packet.pkType)) + "\n");
        builder.append("流水号:" + ByteUtil.bytesToHexSpaceString(ByteUtil.shortToBytes((short) packet.pktId)) + "\n");
        builder.append("保留字节:" + ByteUtil.bytesToHexSpaceString(ByteUtil.intToBytes(packet.keep)) + "\n");
        builder.append("cmd:" + ByteUtil.bytesToHexSpaceString(ByteUtil.intToBytes(packet.cmd)) + "\n");
        builder.append("数据长度:" + ByteUtil.bytesToHexSpaceString(ByteUtil.intToBytes(packet.cmdDataLength)) + "\n");
        builder.append("数据:" + ByteUtil.bytesToHexSpaceString(packet.data) + "\n");
        builder.append("结尾值:" + ByteUtil.bytesToHexSpaceString(ByteUtil.intToBytes(Packet.END_FRAME)) + "\n");
        MyLog.e(builder.toString());


        if (packet.cmd == CMD.RECIVE_SOR_FILE) {
            if (packet.data.length < 32 + 4 + 32) return;

            String fileName = ByteUtil.bytes2Str(ByteUtil.subBytes(packet.data, 0, 32));
            int fileSize = ByteUtil.bytesToInt(ByteUtil.subBytes(packet.data, 32, 4));
            String MD5 = ByteUtil.bytes2Str(ByteUtil.subBytes(packet.data, 32 + 4, 32));
            byte[] data = ByteUtil.subBytes(packet.data, 32 + 4 + 32, packet.data.length - (32 + 4 + 32));

            File file = FileHelper.saveFileToLocal(data, false, fileName);
            if (file.length() >= fileSize) {
                try {
                    String fileMD5 = MD5Utils.getFileMD5(file);
                    SorFileBean bean = new SorFileBean();
                    bean.fileName = fileName;
                    bean.fileSize = fileSize;
                    bean.MD5 = MD5;
                    Log.e("zzz", "sermd5 = " + MD5 + " serfilesize = " + fileSize
                            + " file:" + file.getAbsolutePath()+" filesize = "+file.length()+"  fileMd5="+fileMD5);
                    if (TextUtils.equals(MD5, fileMD5)) {
                        EventBus.getDefault().post(bean, EventBusTag.SOR_RECIVE_SUCCESS);
                    } else {
                        EventBus.getDefault().post(bean, EventBusTag.SOR_RECIVE_FAIL);
                        file.delete();
                    }
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    @Subscriber(tag = EventBusTag.TAG_SEND_MSG)
    public void sendMsg(Packet packet) {
        StringBuilder builder = new StringBuilder();
        if (packet.cmd == CMD.GET_DEVICE_SERIAL_NUMBER) {
            builder.append("发送获取设备号命令成功\n");
        }
        builder.append("起始值:" + ByteUtil.bytesToHexSpaceString(ByteUtil.intToBytes(Packet.START_FRAME)) + "\n");
        builder.append("总帧长度:" + ByteUtil.bytesToHexSpaceString(ByteUtil.intToBytes(packet.pkAllLen)) + "\n");
        builder.append("版本号:" + ByteUtil.bytesToHexSpaceString(ByteUtil.intToBytes(packet.rev)) + "\n");
        builder.append("源地址:" + ByteUtil.bytesToHexSpaceString(ByteUtil.intToBytes(packet.src)) + "\n");
        builder.append("目标地址:" + ByteUtil.bytesToHexSpaceString(ByteUtil.intToBytes(packet.dst)) + "\n");
        builder.append("帧类型:" + ByteUtil.bytesToHexSpaceString(ByteUtil.shortToBytes(packet.pkType)) + "\n");
        builder.append("流水号:" + ByteUtil.bytesToHexSpaceString(ByteUtil.shortToBytes((short) packet.pktId)) + "\n");
        builder.append("保留字节:" + ByteUtil.bytesToHexSpaceString(ByteUtil.intToBytes(packet.keep)) + "\n");
        builder.append("cmd:" + ByteUtil.bytesToHexSpaceString(ByteUtil.intToBytes(packet.cmd)) + "\n");
        builder.append("数据长度:" + ByteUtil.bytesToHexSpaceString(ByteUtil.intToBytes(packet.cmdDataLength)) + "\n");
        builder.append("数据:" + ByteUtil.bytesToHexSpaceString(packet.data) + "\n");
        builder.append("结尾值:" + ByteUtil.bytesToHexSpaceString(ByteUtil.intToBytes(Packet.END_FRAME)) + "\n");
        MyLog.e(builder.toString());
    }


}
