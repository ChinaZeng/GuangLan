package com.zzw.socketdemo.socket.resolve;

import com.zzw.socketdemo.socket.CMD;
import com.zzw.socketdemo.socket.utils.ByteUtil;

import java.net.Socket;

/**
 * 这里以后可以维护一个池
 */
public class PacketHelper {

    /**
     * APP询问设备序列号
     *
     * @param socket
     * @return
     */
    public static Packet getDeviceSerialNumberPacket(Socket socket) {
        Packet packet = new Packet(socket, Packet.TYPE.SEND);
        packet.cmd = CMD.GET_DEVICE_SERIAL_NUMBER;
        return packet;
    }

    /**
     * APP给设备下发OTDR测试参数并启动测试
     *
     * @param socket
     * @param args   参数
     * @return
     */
    public static Packet getTestArgsAndStartTestPacket(Socket socket, int... args) {
        Packet packet = new Packet(socket, Packet.TYPE.SEND);
        packet.cmd = CMD.SEND_TEST_ARGS_AND_START_TEST;
        packet.putData(ByteUtil.intToBytes(args[0]));
        packet.putData(ByteUtil.intToBytes(args[1]));
        packet.putData(ByteUtil.intToBytes(args[2]));
        packet.putData(ByteUtil.intToBytes(args[3]));
        packet.putData(ByteUtil.intToBytes(args[4]));
        packet.putData(ByteUtil.intToBytes(args[5]));
        return packet;
    }

    /**
     * APP向设备发送停止OTDR测试命令
     *
     * @param socket
     * @return
     */
    public static Packet getTestArgsAndStopTestPacket(Socket socket) {
        Packet packet = new Packet(socket, Packet.TYPE.SEND);
        packet.cmd = CMD.SEND_TEST_ARGS_AND_STOP_TEST;
        return packet;
    }

    /**
     * @param socket
     * @param fileName 文件名称  32
     * @param fileDir  文件存放位置 16
     * @return
     */
    public static Packet getSorFilePacket(Socket socket, String fileName, String fileDir) {
        Packet packet = new Packet(socket, Packet.TYPE.SEND);
        packet.cmd = CMD.GET_SOR_FILE;
        byte[] fileNameBA = new byte[32];
        byte[] ft = ByteUtil.stringToUTF8Bytes(fileName);
        System.arraycopy(ft, 0, fileNameBA, 0, ft.length <= 32 ? ft.length : 32);
        byte[] fileDirBA = new byte[16];
        byte[] fdt = ByteUtil.stringToUTF8Bytes(fileDir);
        System.arraycopy(fdt, 0, fileDirBA, 0, fdt.length <= 16 ? fdt.length : 16);
        packet.putData(fileNameBA);
        packet.putData(fileDirBA);
        return packet;
    }


    /**
     * 心跳
     *
     * @param socket
     * @param isSend =true  发送   false 回复
     * @return
     */
    public static Packet getHeartPacket(Socket socket, boolean isSend) {
        Packet packet = new Packet(socket, Packet.TYPE.SEND);
        if (isSend) {
            packet.cmd = CMD.HEART_SEND;
        } else {
            packet.cmd = CMD.HEART_RE;
        }
        return packet;
    }

    /**
     * 回复
     *
     * @param socket
     * @param errorCode 错误代码
     * @param cmdCode   命令码  Uint32
     * @return
     */
    public static Packet getRePacket(Socket socket, int errorCode, int cmdCode) {
        Packet packet = new Packet(socket, Packet.TYPE.SEND);
        packet.cmd = CMD._RE;
        packet.putData(ByteUtil.intToBytes(errorCode));
        packet.putData(ByteUtil.intToBytes(cmdCode));
        return packet;
    }


    /**
     * 发送sor文件流
     *
     * @param socket
     * @return
     */
    public static Packet getSendSorFilePacket(Socket socket, String fileName, String md5, int fileSize, byte[] data) {
        Packet packet = new Packet(socket, Packet.TYPE.SEND);
        packet.cmd = CMD._FILE;

        byte[] fileNameBA = new byte[32];
        byte[] ft = ByteUtil.stringToUTF8Bytes(fileName);
        System.arraycopy(ft, 0, fileNameBA, 0, ft.length <= 32 ? ft.length : 32);

        packet.putData(fileNameBA);

        packet.putData(ByteUtil.intToBytes(fileSize));

        byte[] md5BA = new byte[32];
        byte[] md5B = ByteUtil.stringToUTF8Bytes(md5);
        System.arraycopy(md5B, 0, md5BA, 0, md5B.length <= 32 ? md5B.length : 32);
        ByteUtil.bytes2Str(md5B);
        packet.putData(md5BA);

        packet.putData(data);

        return packet;
    }


    /**
     * @param socket
     * @param fileName 文件名称  32
     * @param fileDir  文件存放位置 16
     * @return
     */
    public static Packet getSendSorInfo(Socket socket, String fileName, String fileDir, int size) {
        Packet packet = new Packet(socket, Packet.TYPE.SEND);
        packet.cmd = CMD.RECIVE_SOR_INFO;
        byte[] fileNameBA = new byte[32];
        byte[] ft = ByteUtil.stringToUTF8Bytes(fileName);
        System.arraycopy(ft, 0, fileNameBA, 0, ft.length <= 32 ? ft.length : 32);
        byte[] fileDirBA = new byte[16];
        byte[] fdt = ByteUtil.stringToUTF8Bytes(fileDir);
        System.arraycopy(fdt, 0, fileDirBA, 0, fdt.length <= 16 ? fdt.length : 16);
        packet.putData(fileNameBA);
        packet.putData(fileDirBA);
        packet.putData(ByteUtil.intToBytes(size));
        return packet;
    }

}
