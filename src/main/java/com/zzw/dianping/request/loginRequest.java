package com.zzw.dianping.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class loginRequest {

    @NotBlank(message = "手机号不能为空")
    private String telphone;

    @NotBlank(message = "密码不能为空")
    private String password;

    public loginRequest(String telphone, String password, String nickName, Integer gender) {
        this.telphone = telphone;
        this.password = password;

    }

    public String getTelphone() {
        return telphone;
    }

    public void setTelphone(String telphone) {
        this.telphone = telphone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
