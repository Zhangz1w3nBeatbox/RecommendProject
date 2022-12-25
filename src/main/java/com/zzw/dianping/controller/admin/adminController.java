package com.zzw.dianping.controller.admin;

import com.zzw.dianping.common.*;
import com.zzw.dianping.model.userModel;
import com.zzw.dianping.request.registerRequest;
import com.zzw.dianping.service.UserService;
import com.zzw.dianping.service.categoryService;
import com.zzw.dianping.service.sellerService;
import com.zzw.dianping.service.shopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Controller("/admin/admin")
@RequestMapping("/admin/admin")
public class adminController {


    @Value("${admin.email}")
    private String email;

    @Value("${admin.encryptPassword}")
    private String encryptPassword;


    public static final String CURRENT_ADMIN_SESSION = "currentAdminSession";

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    public UserService userService;

    @Autowired
    public shopService shopService;

    @Autowired
    public sellerService sellerService;

    @Autowired
    public categoryService categoryService;


    //admin后台首页-html版本
    @RequestMapping("/index")
    @AdminPermission
    public ModelAndView index() {

        ModelAndView modelAndView = new ModelAndView("/admin/admin/index");

        modelAndView.addObject("userCount",userService.countAllUser());
        modelAndView.addObject("shopCount",shopService.countAllShop());
        modelAndView.addObject("sellerCount",sellerService.countAllSeller());
        modelAndView.addObject("categoryCount",categoryService.countAllCategory());

        modelAndView.addObject("CONTROLLER_NAME","admin");
        modelAndView.addObject("ACTION_NAME","index");

        return modelAndView;
    }
//    //admin后台首页-json版本
//    @RequestMapping("/index")
//    @AdminPermission(produceType = "application/json")
//    @ResponseBody
//    public commonRes index() {
//        return commonRes.creat(null);
//    }

    //admin后台首页
    @RequestMapping("/loginPage")
    public ModelAndView loginPage() {
        ModelAndView modelAndView = new ModelAndView("/admin/admin/login");
        return modelAndView;
    }


    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public String  login(@RequestParam(name = "email") String email,@RequestParam(name = "password") String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {

        if(StringUtils.isEmpty(email)||StringUtils.isEmpty(password)){
            throw  new BusinessException(EmBusinessError.Validate_Parameter_Error,"管理员账户或者密码不能为空");
        }


        if(this.email.equals(email)&&this.encryptPassword.equals(encodeByMD5(password))){
            //登陆成功
            //session存储
            httpServletRequest.getSession().setAttribute(CURRENT_ADMIN_SESSION,this.email);

            //重定向到admin的首页
            return "redirect:/admin/admin/index";
        }else{
            throw  new BusinessException(EmBusinessError.Validate_Parameter_Error,"管理员账户或者密码错误");
        }
    }


    private String encodeByMD5(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64Encoder = new BASE64Encoder();
        String md5Password = base64Encoder.encode(md5.digest(password.getBytes("utf-8")));
        return md5Password;
    }





    @RequestMapping("/get")
    @ResponseBody
    public commonRes get(@RequestParam(name = "id") Integer id) throws BusinessException {
        userModel userModel = userService.getUser(id);

        if(userModel==null){
//            EmBusinessError noObjectFound = EmBusinessError.NO_OBJECT_FOUND;
//            commonError errorData = new commonError(noObjectFound);
//            commonRes failRes = commonRes.creat(errorData, "fail");
//            return failRes;

            throw new BusinessException(EmBusinessError.NO_OBJECT_FOUND);
        }

        return commonRes.creat(userModel);
    }





    //用户注册方法
    @RequestMapping("/register")
    @ResponseBody
    public commonRes register(@Valid @RequestBody registerRequest registerRequest, BindingResult bindingResult) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {

        //参数校验
        if(bindingResult.hasErrors()){
            throw new BusinessException(EmBusinessError.Validate_Parameter_Error, commonUtils.precessErrorString(bindingResult));
        }

        userModel userModel = new userModel();
        userModel.setTelphone(registerRequest.getTelphone());
        userModel.setPassword(registerRequest.getPassword());
        userModel.setNickName(registerRequest.getNickName());
        userModel.setGender(registerRequest.getGender());

        userModel registerUser = userService.registerUser(userModel);

        return commonRes.creat(registerUser);
    }


    //用户注销
    @RequestMapping("/logout")
    @ResponseBody
    public commonRes logout(){

        httpServletRequest.getSession().invalidate();

        return commonRes.creat(null);
    }


}
