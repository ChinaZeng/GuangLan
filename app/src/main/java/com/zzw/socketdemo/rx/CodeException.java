package com.zzw.socketdemo.rx;

/**
 * Created by zzw on 2018/10/3.
 * 描述:
 */
public class CodeException extends Exception {

    private int code;
    private String msg;

    public CodeException() {
    }
    public CodeException(int code) {
        this.code = code;
    }

    public CodeException(int code, String message) {
        this.msg = message;
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
