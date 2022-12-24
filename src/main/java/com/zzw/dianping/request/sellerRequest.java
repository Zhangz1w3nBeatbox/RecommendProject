package com.zzw.dianping.request;

import javax.validation.constraints.NotBlank;

public class sellerRequest {

    @NotBlank(message = "商户名称不能为空")
    private String name;

    public sellerRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
