package com.zzw.dianping.common;

public class commonRes {

    //返回状态
    private String status;

    //返回数据
    private Object data;


    //返回正常

    public static commonRes creat(Object res){
        return commonRes.creat(res,"successful");
    }


    public static commonRes creat(Object res,String status){
        commonRes commonRes = new commonRes(status,res);
        return commonRes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public commonRes(String status, Object data) {
        this.status = status;
        this.data = data;
    }
}
