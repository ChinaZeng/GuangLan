package com.zzw.guanglan.socket.thread;

import com.zzw.guanglan.socket.listener.SocketMessageListenerAdapter;
import com.zzw.guanglan.socket.listener.SocketThreadStatusListener;
import com.zzw.guanglan.socket.resolve.Packet;
import com.zzw.guanglan.socket.utils.KeyUtils;
import com.zzw.guanglan.socket.utils.MyLog;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;

/**
 * 监听线程
 */
public class ListenerThread extends Thread {
    private HashMap<String, SocketThread> serverThreads = new HashMap<>();

    private static final String TAG = "ListenerThread";
    private ServerSocket serverSocket = null;
    private int port;
    private boolean flog = true;
    private SocketThreadStatusListener socketThreadStatusListener;

    public ListenerThread(int port) {
        setName("ListenerThread");
        this.port = port;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        while (flog) {
            try {

                if (serverSocket == null)
                    return;

                //阻塞，等待设备连接
                MyLog.e(TAG, "服务端连接阻塞");
                Socket socket = serverSocket.accept();

                if (!flog) {
                    break;
                }
                MyLog.e(TAG, "客户端连接  ip：" + socket.getInetAddress() + " port:" + socket.getPort());
                String key = KeyUtils.getKey(socket);
                SocketThread s = serverThreads.remove(key);
                if (s != null) {
                    s.exit();
                    Thread.sleep(500);
                }

                ServerThread serverThread = new ServerThread(socket, socketThreadStatusListener);
                serverThread.addListener(new SocketMessageListenerAdapter() {
                    @Override
                    public Packet onSendMsgAgo(SocketThread socketThread, boolean isSuccess, Packet packet) {
                        if (!isSuccess) {
                            //TODO 发送消息失败触发关闭  本来应该用心跳触发关闭的 。这里就不那么麻烦了
                            SocketThread s = serverThreads.remove(packet.key());
                            if (s != null) {
                                s.exit();
                            }
                        }
                        return packet;
                    }
                });
                serverThread.start();
                serverThreads.put(key, serverThread);
            } catch (IOException e) {
                MyLog.e(TAG, " error:" + e.getMessage());
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void exit() {
        flog = false;
        Collection<SocketThread> socketThreads = serverThreads.values();
        for (SocketThread socketThread : socketThreads) {
            socketThread.exit();
        }
    }

    public HashMap<String, SocketThread> getServerThreads() {
        return serverThreads;
    }

    public void setSocketThreadStatusListener(SocketThreadStatusListener socketThreadStatusListener) {
        this.socketThreadStatusListener = socketThreadStatusListener;
    }

}
