package com.zzw.socketdemo.socket;

import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Arrays;

public class Packet {
    enum TYPE {
        SEND,
        RECIVER
    }

    private String id = System.currentTimeMillis() + "";
    private TYPE type;
    private final Socket socket;
    private byte[] data;


    public String getId() {
        return id;
    }

    public boolean isSend() {
        return type == TYPE.SEND;
    }

    public Packet(Socket socket, TYPE type) {
        this(socket, type, new byte[0]);
    }

    public Packet(Socket socket, TYPE type, int len) {
        this(socket, type, new byte[len]);
    }

    public Packet(Socket socket, TYPE type, byte[] data) {
        this.socket = socket;
        this.type = type;
        this.data = data;
    }

    public byte[] data() {
        return data;
    }

    public void putByteArray(byte[] content) {
        byte[] dstData = new byte[data.length + content.length];
        System.arraycopy(data, 0, dstData, 0, data.length);
        System.arraycopy(content, 0, dstData, data.length, content.length);
        this.data = dstData;
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

    public String hexString() {
        return Arrays.toString(data);
    }

    public Packet clone(Packet packet) {
        return new Packet(packet.socket, packet.type, packet.data);
    }

    public String ip() {
        return socket.getInetAddress().toString();
    }

    public int port() {
        return socket.getPort();
    }
}
