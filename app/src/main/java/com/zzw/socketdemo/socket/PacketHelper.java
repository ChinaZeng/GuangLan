package com.zzw.socketdemo.socket;

import java.net.Socket;

/**
 * 这里以后可以维护一个池
 */
public class PacketHelper {


    public static Packet getTextMsgPacket(Socket socket){
        Packet packet = new Packet(socket, Packet.TYPE.SEND);
        packet.cmd = CMD.CMD_TEXT_MSG;
        return packet;
    }

    public static Packet getFileMsgPacket(Socket socket){
        Packet packet = new Packet(socket, Packet.TYPE.SEND);
        packet.cmd = CMD.CMD_FILE_MSG;
        return packet;
    }
}
