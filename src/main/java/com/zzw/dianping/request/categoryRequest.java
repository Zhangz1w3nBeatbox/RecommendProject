package com.zzw.dianping.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class categoryRequest {

    @NotBlank(message = "名称不能为空")
    private String name;

    @NotBlank(message = "iconUrl名称不能为空")
    private String iconUrl;

    @NotNull(message = "权重不能为空")
    private Integer sort;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}
