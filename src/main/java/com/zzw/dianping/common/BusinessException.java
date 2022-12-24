package com.zzw.dianping.common;

public class BusinessException extends Exception{
    private commonError commonError;

    public BusinessException(EmBusinessError emBusinessError) {
        super();
        this.commonError = new commonError(emBusinessError);
    }
    public BusinessException(EmBusinessError emBusinessError,String errorMsg) {
        super();
        this.commonError = new commonError(emBusinessError);
        this.commonError.setErrorMsg(errorMsg);
    }
    public commonError getCommonError() {
        return commonError;
    }

    public void setCommonError(commonError commonError) {
        this.commonError = commonError;
    }
}
