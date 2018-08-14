package com.zzw.socketdemo.socket;

import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Arrays;

public class Packet {
    enum TYPE {
        SEND,
        RECIVER
    }

    private TYPE type;
    private final Socket socket;

    //起始帧 4
    public static final int START_FRAME = 0xffffeeee;
    //结束帧 4
    public static final int END_FRAME = 0xeeeeffff;
    //总长度 4
    public int pkAllLen;
    //版本号 v1.000   4

    public static final int VERSION_1000 = 0x001B0000;

    public int rev;

    private final static int APP = 0x0000004e;
    private final static int TF500 = 0xA000000e;
    //源地址 4
    public int src;
    //目的地址 4
    public int dst;
    //帧类型  2
    public short pkType = 0;

    public static int id = 0;
    //流水号 0-65536   unsigned int  (short) 依次递增 循环归0   2位
    public int pktId;
    //保留位
    public int keep = 0;
    //命令码
    public int cmd = 0;
    //数据长度 = 命令码+数据长度+数据区的字节长度
    public int cmdDataLength = 0;
    //数据区
    public byte[] data = new byte[0];


    public int size() {
        //起始帧(4) + 总帧长(4) + 版本号(4) + 源地址(4) + 目标地址(4) + 帧类型(2)
        // + 流水号(2) + 保留字节(4) + 命令码(4) +数据长度(4)+数据区域（data.length） +结尾帧(4)

        return 4 * 5 + 2 * 2 + 4 * 3 + data.length + 4;
    }

    public boolean isSend() {
        return type == TYPE.SEND;
    }

    public Packet(Socket socket, TYPE type) {
        this.socket = socket;
        this.type = type;
    }

    public byte[] sendPktData() {
        byte[] startBA = ByteUtil.intToBytes(START_FRAME);

        pkAllLen = size();
        byte[] pkAllLenBA = ByteUtil.intToBytes(pkAllLen);
        rev = VERSION_1000;
        byte[] revBA = ByteUtil.intToBytes(rev);
        src = APP;
        byte[] srcBA = ByteUtil.intToBytes(src);
        dst = TF500;
        byte[] dstBA = ByteUtil.intToBytes(dst);

        byte[] pkTypeBA = ByteUtil.shortToBytes(pkType);

        Packet.id++;
        if (Packet.id >= 65535) {
            Packet.id = 0;
        }

        pktId = Packet.id;
        byte[] idBA = ByteUtil.shortToBytes((short) pktId);

        byte[] keepBA = ByteUtil.intToBytes(keep);
        byte[] cmdBA = ByteUtil.intToBytes(cmd);

        cmdDataLength = 4 + 4 + data.length;
        byte[] cmdDataLengthBA = ByteUtil.intToBytes(cmdDataLength);

        byte[] endBA = ByteUtil.intToBytes(END_FRAME);
        return ByteUtil.mergerBytes(startBA,
                pkAllLenBA,
                revBA,
                srcBA,
                dstBA,
                pkTypeBA,
                idBA,
                keepBA,
                cmdBA,
                cmdDataLengthBA,
                data,
                endBA);
    }


    public void putData(byte[] content) {
        this.data = ByteUtil.mergerByte(data, content);
    }

    public String key() {
        return KeyUtils.getKey(socket);
    }

    public String string() {
        try {
            return new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String ip() {
        return socket.getInetAddress().toString();
    }

    public int port() {
        return socket.getPort();
    }
}
