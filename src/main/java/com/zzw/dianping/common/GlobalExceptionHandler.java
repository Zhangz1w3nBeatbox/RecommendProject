package com.zzw.dianping.common;

import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public commonRes doError(HttpServletRequest request, HttpServletResponse response,Exception exception){
        if(exception instanceof BusinessException){
            BusinessException businessException = (BusinessException) exception;
            return commonRes.creat(businessException.getCommonError(),"fail");
        }else if(exception instanceof NoHandlerFoundException){
            commonError error = new commonError(EmBusinessError.NO_HANDLER_FOUND);
            return  commonRes.creat(error,"fail");
        }else if(exception instanceof ServletRequestBindingException){
            commonError error = new commonError(EmBusinessError.Request_Binding_Exception);
            return  commonRes.creat(error,"fail");
        }
        else{
            commonError error = new commonError(EmBusinessError.UNKNOWN_ERROR);
            return  commonRes.creat(error,"fail");
        }
    }
}
