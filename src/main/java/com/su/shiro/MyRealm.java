package com.su.shiro;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.su.common.UnauthorizedException;
import com.su.pojo.User;
import com.su.pojo.view.JwtToken;
import com.su.pojo.view.UserView;
import com.su.utils.JWTUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class MyRealm extends AuthorizingRealm {

    @Autowired
    RedisTemplate<Object,Object> redisTemplate;

    //判断令牌是否支持
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }

    //认证(判断是否登录)
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken auth) throws AuthenticationException {
        String token = (String) auth.getCredentials();
        //解析token，获取用户名
        String userName = JWTUtils.getUserName(token);
        if (userName==null){
            throw new AuthenticationException("请登录后再操作");
        }
        //redis中查询，并验证码用户信息是否存在
        User user = (User) redisTemplate.opsForValue().get(userName);
        if (user==null){
            throw new AuthenticationException("请登录后再操作");
        }
        //验证jwt是否正确
        UserView userView = new UserView();
        userView.setPassword(user.getPassword());
        userView.setUserName(user.getUserName());
        if (!JWTUtils.verify(token,userView)){
            throw new AuthenticationException("请登录后再操作");
        }
        return new SimpleAuthenticationInfo(token,token,"");
    }
}
