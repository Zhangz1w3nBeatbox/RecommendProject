package com.zzw.dianping.common;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

public class commonUtils {

    //对应的bindingResult转换成字符串
    public  static  String precessErrorString(BindingResult bindingResult){
        if(!bindingResult.hasErrors()){
            return "";
        }

        StringBuffer stringBuffer = new StringBuffer();

        for(FieldError fieldError:bindingResult.getFieldErrors()){
            stringBuffer.append(fieldError.getDefaultMessage()+",");
        }

        return stringBuffer.substring(0,stringBuffer.length()-1);
    }

}
