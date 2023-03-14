package com.su.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.su.pojo.view.JwtToken;
import com.su.pojo.view.UserView;

import java.io.UnsupportedEncodingException;
import java.util.Date;

//生成token，解析token
public class JWTUtils {
    //默认七天过期
    private static final long time_out=4*24*60*60*1000;

    //生成token
    // TODO 将token放入缓存
    public static String createToken(UserView userView){
        try {
            Date date = new Date(System.currentTimeMillis()+time_out);
            //生成签名
            Algorithm algorithm = Algorithm.HMAC256(userView.getPassword());
            return JWT.create()
                    //添加用户名到token中
                    .withClaim("username",userView.getUserName())
                    .withExpiresAt(date)
                    .sign(algorithm);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    //通过token获取用户名
    public static String getUserName(String token){
        try {
            DecodedJWT decode = JWT.decode(token);
            return decode.getClaim("username").asString();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    //检验token是否正确（是否被修改）
    public static boolean verify(String token,UserView userView){
        try {
            Algorithm algorithm = Algorithm.HMAC256(userView.getPassword());
            JWTVerifier verifier = JWT.require(algorithm)
                    .withClaim("username", userView.getUserName())
                    .build();
            DecodedJWT verify = verifier.verify(token);
            System.out.println("JWT正常");
            return true;
        } catch (UnsupportedEncodingException e) {
            return false;
        }

    }
}
