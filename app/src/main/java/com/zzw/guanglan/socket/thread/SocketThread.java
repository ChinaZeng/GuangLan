package com.zzw.guanglan.socket.thread;

import android.os.SystemClock;

import com.zzw.guanglan.socket.utils.MyLog;
import com.zzw.guanglan.socket.resolve.Packet;
import com.zzw.guanglan.socket.listener.STATUS;
import com.zzw.guanglan.socket.resolve.SocketReader;
import com.zzw.guanglan.socket.resolve.SocketSender;
import com.zzw.guanglan.socket.listener.SocketMessageListener;
import com.zzw.guanglan.socket.listener.SocketThreadStatusListener;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SocketThread extends Thread {

//    private LinkedBlockingQueue<Packet> packetsQueue = new LinkedBlockingQueue<>();

    private volatile boolean flog = true;
    public final Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;

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
    public SocketSender socketSender;

    @Override
    public void run() {
        if (socket == null)
            return;
        socketSender = new SocketSender(this);
        init();

        //获取数据流
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            if (socketThreadStatusListener != null) {
                socketThreadStatusListener.onStatusChange(this, STATUS.RUNNING);
            }

//            new SendDataThread().start();

            while (flog) {
                //5秒
//                if (count > 5 * 5) {
//                    break;
//                }
                SystemClock.sleep(5);
//                if (!socket.isConnected()) {
//                    Thread.sleep(200);
//                    count++;
//                    continue;
//                }
//                count = 0;
                if (socket.isConnected() && inputStream.available() > 0) {
                    Packet packet = SocketReader.readPktData(socket, inputStream);
                    if (packet != null) {
                        onReciveMsg(packet);
                    }
                }
            }
        } catch (IOException e) {
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
    }


    private void close() {
        flog = false;
        socketSender.shutdownNow();
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


    public void sendQueue(Packet packet) {
        realSendData(packet);

//        try {
//            packetsQueue.put(packet);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    public void realSendData(Packet packet) {
        if (flog && socket.isConnected() && outputStream != null) {
            try {
                onSendMsgBefore(packet);
                outputStream.write(packet.sendPktData());
//                outputStream.flush();
                onSendMsgAgo(true, packet);
            } catch (IOException e) {
                e.printStackTrace();
                onSendMsgAgo(false, packet);
            }
        } else {
            onSendMsgAgo(false, packet);
        }
    }


    public void exit() {
        flog = false;
    }


    private void onSendMsgAgo(boolean sendSuccess, Packet packet) {
        for (SocketMessageListener listener : listeners) {
            if (packet != null) {
                packet = listener.onSendMsgAgo(this, sendSuccess, packet);
            }
        }
    }

    private void onReciveMsg(Packet packet) {
        for (SocketMessageListener listener : listeners) {
            if (packet != null) {
                packet = listener.onReciveMsg(this, packet);
            }
        }
    }

    private void onSendMsgBefore(Packet packet) {
        for (SocketMessageListener listener : listeners) {
            if (packet != null) {
                packet = listener.onSendMsgBefore(this, packet);
            }
        }
    }


    public void addListener(SocketMessageListener listener) {
        if (listener == null)
            return;
        listeners.add(listener);
    }


//    class SendDataThread extends Thread {
//
//        @Override
//        public void run() {
//            while (flog) {
//                try {
//                    Packet packet = packetsQueue.take();
//                    if (packet != null) {
//                        realSendData(packet);
//                    }
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }
//    }
}
