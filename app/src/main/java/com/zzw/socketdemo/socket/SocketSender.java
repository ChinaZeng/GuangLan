package com.zzw.socketdemo.socket;

public class SocketSender {

    private SocketThread socketThread;

    public SocketSender(SocketThread socketThread) {
        this.socketThread = socketThread;
    }

    /**
     * APP询问设备序列号
     */
    public void getDeviceSerialNumber() {
        Dispatcher.getInstance().submit(new Runnable() {
            @Override
            public void run() {
                Packet packet = PacketHelper.getDeviceSerialNumberPacket(socketThread.socket);
                socketThread.sendQueue(packet);
            }
        });
    }


    /**
     * APP给设备下发OTDR测试参数并启动测试
     */
    public void sendTestArgsAndStartTest(final int... args) {
        Dispatcher.getInstance().submit(new Runnable() {
            @Override
            public void run() {
                Packet packet = PacketHelper.getTestArgsAndStartTestPacket(socketThread.socket, args);
                socketThread.sendQueue(packet);
            }
        });
    }

    /**
     * @param fileName 文件名称  16
     * @param fileDir  文件存放位置 48
     */
    public void getSorFile(final String fileName, final String fileDir) {
        Dispatcher.getInstance().submit(new Runnable() {
            @Override
            public void run() {
                Packet packet = PacketHelper.getSorFilePacket(socketThread.socket, fileName, fileDir);
                socketThread.sendQueue(packet);
            }
        });
    }

    /**
     * 发送心跳
     */
    public void sendHeart() {
        Dispatcher.getInstance().submit(new Runnable() {
            @Override
            public void run() {
                Packet packet = PacketHelper.getHeartPacket(socketThread.socket, true);
                socketThread.sendQueue(packet);
            }
        });
    }

    /**
     * 回复心跳
     */
    public void reHeart() {
        Dispatcher.getInstance().submit(new Runnable() {
            @Override
            public void run() {
                Packet packet = PacketHelper.getHeartPacket(socketThread.socket, false);
                socketThread.sendQueue(packet);
            }
        });
    }

    /**
     * 回复
     *
     * @param errorCode 错误代码
     * @param cmdCode   命令码  Uint32
     */
    public void sendRe(final int errorCode, final int cmdCode) {
        Dispatcher.getInstance().submit(new Runnable() {
            @Override
            public void run() {
                Packet packet = PacketHelper.getRePacket(socketThread.socket, errorCode, cmdCode);
                socketThread.sendQueue(packet);
            }
        });
    }


    private final static int FILE_BUFFER = 4096;

    public void sendFileMsg(final String path) {
//        Dispatcher.getInstance().submit(new Runnable() {
//            @Override
//            public void run() {
//                InputStream is = null;
//                try {
//                    Packet packetStart = PacketHelper.getFileMsgPacket(socket);
//                    packetStart.flog = CMD.FLOG.FLOG_FILE_START;//表示开始
//                    sendQueue(packetStart);
//
//                    File file = new File(path);
//                    is = new FileInputStream(file);
//                    byte[] buffer = new byte[FILE_BUFFER];
//                    int len;
//                    while ((len = is.read(buffer, 0, buffer.length)) > 0) {
//                        Packet packetData = PacketHelper.getFileMsgPacket(socket);
//                        packetData.flog = CMD.FLOG.FLOG_FILE_DATA;//内容
//
//                        byte[] data = buffer;
//                        if (len < buffer.length) {
//                            data = ByteUtils.subBytes(buffer, 0, len);
//                        }
//                        packetData.data = data;
//                        sendQueue(packetData);
//                    }
//
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } finally {
//                    closeCloseable(is);
//                    Packet packetEnd = PacketHelper.getFileMsgPacket(socket);
//                    packetEnd.flog = CMD.FLOG.FLOG_FILE_END;//表示结束
//                    sendQueue(packetEnd);
//                }
//            }
//        });
    }


}
