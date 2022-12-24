package com.zzw.dianping.common;


//枚举类-
public enum EmBusinessError {

    NO_OBJECT_FOUND(10001,"请求对象不存在"),
    UNKNOWN_ERROR(10002,"未知错误"),

    NO_HANDLER_FOUND(10003,"找不到路径错误"),

    Request_Binding_Exception(10004,"参数绑定异常,请检测参数是否有误!"),

    Validate_Parameter_Error(10005,"请求参数校验异常,请检测参数是否填写!"),

    REGISTER_DUP_FAIL(20001,"用户已存在！"),

    Login_Fail(20002,"手机号或者密码错误！"),

    Admin_Login_Fail(30001,"admin需要登录"),

    Category_Name_Duplicated(40001,"品类名已经存在"),;

    private Integer errorCode;

    private String errorMsg;



    EmBusinessError(Integer errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
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
