package com.zzw.dianping.common;

public class commonError {


    private Integer errorCode;

    private String errorMsg;

    public commonError(Integer errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    //利用枚举
    public commonError(EmBusinessError emBusinessError){
        this.errorCode = emBusinessError.getErrorCode();
        this.errorMsg = emBusinessError.getErrorMsg();
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
