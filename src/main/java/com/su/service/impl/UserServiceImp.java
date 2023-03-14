package com.su.service.impl;

import com.auth0.jwt.JWT;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.su.common.Errors;
import com.su.common.ResultBean;
import com.su.pojo.User;
import com.su.mapper.UserMapper;
import com.su.pojo.view.UserView;
import com.su.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.su.utils.CommonValueUtils;
import com.su.utils.ConstantPropertiesUtils;
import com.su.utils.JWTUtils;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import sun.security.provider.MD5;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author swt 2023-2-20
 * @since 2023-02-20
 */
@Service
public class UserServiceImp extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    @ApiModelProperty("注册")
    @Override
    public ResultBean register(UserView userView) {
        //验证数据是否合法
        if (StringUtils.isEmpty(userView.getUserName())||StringUtils.isEmpty(userView.getCode())||
        StringUtils.isEmpty(userView.getEmail())||StringUtils.isEmpty(userView.getPassword())){
            throw new Errors("500","请将信息填写完整");
        }
        //获取数据
        String email = userView.getEmail();
        String code = userView.getCode();
        String userName = userView.getUserName();
        String password = userView.getPassword();
        //验证验证码是否正确
        String redisKey=email + CommonValueUtils.codeContent;
        String redisCode= (String) redisTemplate.opsForValue().get(redisKey);
        if (redisCode==null){
            throw new Errors("500","验证码过期,请重新发送");
        }
        //密码加密
        String salt=""+System.currentTimeMillis();
        String mdPassword = DigestUtils.md5DigestAsHex((password+salt).getBytes());
        //保存信息到数据库
        User user = new User();
        user.setEmail(email);
        user.setSalt(salt);
        user.setUserName(userName);
        user.setPassword(mdPassword);
        baseMapper.insert(user);
        return ResultBean.success();
    }

    @ApiModelProperty("邮箱发送")
    @Override
    public ResultBean senEmail(String email) {
        //验证数据合法性
        if (StringUtils.isEmpty(email)){
            throw new Errors("500","邮箱不能为空");
        }
        //判断邮箱是否已经注册过
        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("email",email);
        if (baseMapper.selectCount(queryWrapper)>0){
            throw new Errors("500","邮箱已经注册过");
        }
        //拼接key
        String codeContent=email+ CommonValueUtils.codeContent;
        String codeCount=email+CommonValueUtils.codeCount;
        //判断今天验证码发送次数是否超标
        Integer count= (Integer) redisTemplate.opsForValue().get(codeCount);
        if (count!=null&&count>CommonValueUtils.codeNum){
            throw new Errors("500","今天验证码发送次数已经达标，请明天再试");
        }
        //判断验证码一分钟内是否发送过
        String code = (String) redisTemplate.opsForValue().get(codeContent);
        if (code!=null){
            throw new Errors("500","一分钟内已经发送过验证码");
        }
        //随机生成验证码
        code=createCode();
        //发送验证码
        sendCode(email,code);
        //将信息保存入redis
        redisTemplate.opsForValue().set(codeContent,code,60, TimeUnit.SECONDS);
        //设置每天0点过期
        redisTemplate.opsForValue().set(codeCount,count==null?1:count+1,
                getSecond(),TimeUnit.SECONDS);
        return ResultBean.success();
    }

    //计算距0点过期时间的秒数
    public long getSecond(){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return (cal.getTimeInMillis() - System.currentTimeMillis()) / 1000;
    }

    //随机生成验证码
    public String createCode(){
        String result="";
        for (int i = 0; i < 4; i++) {
            Random random=new Random();
            result+=random.nextInt(10);
        }
        return result;
    }

    //发送验证码
    public void sendCode(String email,String code){
        try {
            SimpleEmail simpleEmail = new SimpleEmail();
            // 设置邮箱服务器信息
            simpleEmail.setHostName(ConstantPropertiesUtils.HOST);
            // 设置密码验证器
            simpleEmail.setAuthentication(ConstantPropertiesUtils.EMAIL, ConstantPropertiesUtils.PASSWORD);
            // 设置邮件发送者
            simpleEmail.setFrom(ConstantPropertiesUtils.EMAIL);
            //设置接收者
            simpleEmail.addTo(email);
            // 设置邮件编码
            simpleEmail.setCharset("UTF-8");
            // 设置邮件主题
            simpleEmail.setSubject("程序员面试宝典");
            // 设置邮件内容
            simpleEmail.setMsg("您的验证码为："+code+"，请勿告诉他人！！");
            // 设置邮件发送时间
            simpleEmail.setSentDate(new Date());
            // 发送邮件
            simpleEmail.send();
        } catch (EmailException e) {
            e.printStackTrace();
            throw new Errors("500" ,"邮箱验证码发送错误");
        }
    }


    //登录
    @Override
    public ResultBean login(UserView userView) {
        if (StringUtils.isEmpty(userView.getPassword())
                ||StringUtils.isEmpty(userView.getUserName())){
            throw new Errors("500","账号或密码不能为空");
        }
        //数据库查询
        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("user_name",userView.getUserName());;
        User user = baseMapper.selectOne(queryWrapper);
        //账号错误
        if (user==null){
            throw new Errors("500","密码或账号错误");
        }
        //生成加密密码
        String mdPassword = DigestUtils.
                md5DigestAsHex((userView.getPassword() + user.getSalt()).getBytes());
        //密码错误
        if (!mdPassword.equals(user.getPassword())){
            throw new Errors("500","密码或账号错误");
        }
        //生成token
        userView.setPassword(mdPassword);
        String token = JWTUtils.createToken(userView);
        //用户名缓存到redis
        redisTemplate.opsForValue().set(userView.getUserName(),user,4*24*60*60,
                TimeUnit.SECONDS);
        //封装数据
        Map<String,Object> map=new HashMap<>();
        map.put("user",user);
        map.put("token",token);
        return ResultBean.success().data(map);
    }

    //退出登录
    @Override
    public ResultBean logout(String userName) {
        //删除redis中缓存的用户数据
        redisTemplate.delete(userName);
        return ResultBean.success();
    }


}
