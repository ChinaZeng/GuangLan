package com.zzw.socketdemo;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zzw.socketdemo.socket.CMD;
import com.zzw.socketdemo.socket.resolve.Packet;
import com.zzw.socketdemo.socket.utils.ByteUtil;

import java.util.Arrays;

/**
 * Created by zzw on 2018/9/2.
 * 描述:
 */
public class PacketAdapter extends BaseQuickAdapter<Packet, BaseViewHolder> {
    public PacketAdapter() {
        super(R.layout.item_packet);
    }

    @Override
    protected void convert(BaseViewHolder helper, Packet packet) {
        if (packet == null) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        if (packet.cmd == CMD.GET_DEVICE_SERIAL_NUMBER) {
            builder.append("获取设备号命令\n");
        } else if (packet.cmd == CMD.SEND_TEST_ARGS_AND_START_TEST) {
            builder.append("APP给设备下发OTDR测试参数并启动测试命令\n");
        } else if (packet.cmd == CMD.GET_SOR_FILE) {
            builder.append("APP向设备请求传输sor文件命令\n");
        } else if (packet.cmd == CMD.HEART_SEND) {
            builder.append("心跳包命令\n");
        } else if (packet.cmd == CMD.HEART_RE) {
            builder.append("回复心跳包命令\n");
        } else if (packet.cmd == CMD._RE) {
            builder.append("错误代码命令\n");
        } else if (packet.cmd == CMD.RECIVE_DEVICE_SERIAL_NUMBER) {
            builder.append("OTDR上报设备序列号给APP命令\n");
        } else if (packet.cmd == CMD.RECIVE_TEST_ARGS_AND_START_TEST) {
            builder.append("设备向APP反馈sor文件信息命令\n");
        } else if (packet.cmd == CMD.RECIVE_SOR_FILE) {
            builder.append("设备向APP发送OTDR测试结果文件命令\n");
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

        helper.setText(R.id.tv, builder.toString());
    }
}
