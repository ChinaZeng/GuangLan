package com.zzw.socketdemo.socket;

import org.simple.eventbus.EventBus;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SocketThread extends Thread {

    private ConcurrentLinkedQueue<Packet> packetsQueue = new ConcurrentLinkedQueue<>();

    private boolean flog = true;
    public final Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private final static int BUFFER_SIZE = 1024;

    private List<SocketMessageListener> listeners = new ArrayList<>();
    private SocketThreadStatusListener socketThreadStatusListener;


    public SocketThread(String name, Socket socket, SocketThreadStatusListener socketThreadStatusListener) {
        setName(name);
        MyLog.e(name, "SocketThread <init> name:" + name);
        this.socket = socket;
        this.socketThreadStatusListener = socketThreadStatusListener;
        if (socketThreadStatusListener != null) {
            socketThreadStatusListener.onStatusChange(this, STATUS.INIT);
        }
    }

    private int count;

    @Override
    public void run() {
        if (socket == null)
            return;
        init();

        //获取数据流
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            new SendDataThread().start();

            if (socketThreadStatusListener != null) {
                socketThreadStatusListener.onStatusChange(this, STATUS.RUNNING);
            }

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytes;
            while (flog) {
                //5秒
                if (count > 5 * 5) {
                    break;
                }

                if (!socket.isConnected()) {
                    Thread.sleep(200);
                    count++;
                    continue;
                }
                count = 0;

                Packet packet = new Packet(socket, Packet.TYPE.RECIVER);
                //读取数据 封装packet
                bytes = inputStream.read(buffer);
                if (bytes > 0) {
                    packet.putByteArray(buffer);
                    onReciveMsg(packet);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            MyLog.e("socket退出");
            close();
            if (socketThreadStatusListener != null) {
                socketThreadStatusListener.onStatusChange(this, STATUS.END);
            }
        }
    }

    protected void init() {
        addListener(SocketMessageListener.DEF);
        addListener(new SocketMessageListener() {
            @Override
            public void onReciveMsg(SocketThread socketThread, Packet packet) {
                //TODO 侵入式太高  这里为了省事
                EventBus.getDefault().post(packet, EventBusTag.TAG_RECIVE_MSG);
            }

            @Override
            public void onSendMsgBefore(SocketThread socketThread, Packet packet) {

            }

            @Override
            public void onSendMsgAgo(SocketThread socketThread, boolean isSuccess, Packet packet) {
                if (isSuccess) {
                    EventBus.getDefault().post(packet, EventBusTag.TAG_SEND_MSG);
                }
            }
        });
    }


    private void close() {
        flog = false;

        closeCloseable(inputStream);
        closeCloseable(outputStream);
        inputStream = null;
        outputStream = null;
        try {
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeCloseable(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public void sendData(String content) {
        try {
            sendData(new Packet(socket, Packet.TYPE.SEND, content.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void sendData(byte[] data) {
        sendData(new Packet(socket, Packet.TYPE.SEND, data));
    }

    public void sendData(Packet packet) {
        packetsQueue.add(packet);
    }

    private void realSendData(Packet packet) {
        if (flog && socket.isConnected() && outputStream != null) {
            try {
                onSendMsgBefore(packet);
                outputStream.write(packet.data());
//                outputStream.flush();
                onSendMsgAgo(true, packet);
            } catch (IOException e) {
                e.printStackTrace();
                onSendMsgAgo(false, packet);
            }
        }
    }

    public void exit() {
        flog = false;
    }


    private void onSendMsgAgo(boolean sendSuccess, Packet packet) {
        for (SocketMessageListener listener : listeners) {
            listener.onSendMsgAgo(this, sendSuccess, packet);
        }
    }

    private void onReciveMsg(Packet packet) {
        for (SocketMessageListener listener : listeners) {
            listener.onReciveMsg(this, packet);
        }
    }

    private void onSendMsgBefore(Packet packet) {
        for (SocketMessageListener listener : listeners) {
            listener.onSendMsgBefore(this, packet);
        }
    }


    public void addListener(SocketMessageListener listener) {
        if (listener == null)
            return;
        listeners.add(listener);
    }

    class SendDataThread extends Thread {
        @Override
        public void run() {
            while (flog) {
                if (!packetsQueue.isEmpty()) {
                    Packet packet = packetsQueue.poll();
                    if (packet != null) {
                        realSendData(packet);
                    }
                }
            }
        }
    }
}
