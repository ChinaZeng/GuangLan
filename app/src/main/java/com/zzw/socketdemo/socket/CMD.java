package com.zzw.socketdemo.socket;

public interface CMD {
    byte EMPTY = 0X00;
    byte CMD_TEXT_MSG = 0X01;
    byte CMD_FILE_MSG = 0X02;



    interface FLOG{
        byte FLOG_FILE_START=0x01;
        byte FLOG_FILE_DATA=0x02;
        byte FLOG_FILE_END=0x03;
    }

}
