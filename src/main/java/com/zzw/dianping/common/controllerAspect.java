package com.zzw.dianping.common;


import com.zzw.dianping.controller.admin.adminController;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Aspect
@Configuration
public class controllerAspect {

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private HttpServletResponse httpServletResponse;

    @Around("execution(* com.zzw.dianping.controller.admin.*.*(..)) && @annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public Object validate(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method=((MethodSignature)joinPoint.getSignature()).getMethod();
        AdminPermission adminPermission = method.getAnnotation(AdminPermission.class);
        //看其 是否 被标记为AdminPermission注解
        if(adminPermission==null){
            //看其 是否 被标记为AdminPermission注解
            Object res = joinPoint.proceed();
            return res;
        }else{
            //校验session
            String email = (String) httpServletRequest.getSession().getAttribute(adminController.CURRENT_ADMIN_SESSION);
            if(email==null){
                if(adminPermission.produceType().equals("text/html")){
                    // 校验失败 就 重定向到登陆页面
                    httpServletResponse.sendRedirect("/admin/admin/loginPage");
                    return null;
                }else{
                    commonError commonError =new commonError(EmBusinessError.Admin_Login_Fail);
                    return commonRes.creat(commonError,"fail");
                }

            }else{//成功校验
                Object res = joinPoint.proceed();
                return res;
            }
        }
    }

}
