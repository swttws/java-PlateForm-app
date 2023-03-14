package com.su.controller;


import com.su.common.ResultBean;
import com.su.pojo.User;
import com.su.pojo.view.UserView;
import com.su.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.apache.ibatis.annotations.Param;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author swt 2023-2-20
 * @since 2023-02-20
 */
@Api("用户controller")
@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @ApiModelProperty("邮箱验证码发送")
    @GetMapping("sendEmail")
    public ResultBean sendEmail(@RequestParam String email){
        return userService.senEmail(email);
    }

    @ApiModelProperty("注册")
    @PostMapping("register")
    public ResultBean register(@RequestBody UserView userView){
        return userService.register(userView);
    }

    @ApiModelProperty("登录")
    @PostMapping("/login")
    public ResultBean login(@RequestBody UserView user){
        return userService.login(user);
    }

    @ApiModelProperty("未登录请求")
    @GetMapping("/loginError")
    public ResultBean loginError(){
        return ResultBean.error().message("请登录后再操作");
    }

    @ApiModelProperty("退出登录")
    @GetMapping("/logout")
    public ResultBean logout(@RequestParam("userName") String userName){
        return userService.logout(userName);
    }

    @ApiOperation("查询用户信息")
    @GetMapping("getUser/{userId}")
    public ResultBean getUser(@PathVariable("userId") Integer userId){
        User user = userService.getById(userId);
        return ResultBean.success().data(user);
    }


}















