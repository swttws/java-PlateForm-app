package com.su.common;

import org.apache.shiro.ShiroException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class MyException {

    @ExceptionHandler(com.su.common.Errors.class)
    public ResultBean exception(Errors errors){
        errors.printStackTrace();
        return ResultBean.error().message(errors.getMessage());
    }

    // 捕捉shiro的异常
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(ShiroException.class)
    public ResultBean handle401(ShiroException e) {
        return new ResultBean("401","请登录后再操作",null);
    }

    // 捕捉UnauthorizedException
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    public ResultBean handle401() {
        return new ResultBean("401", "请登录后再操作", null);
    }
}
