package com.su.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class ResubmitAspect {

    @Autowired
    private RedissonClient redissonClient;

    @Pointcut("@annotation(resubmit)")
    public void pointcut(Resubmit resubmit){}

    //防止请求重复提交业务逻辑，使用环绕方法
    @Around("pointcut(resubmit)")
    public void around(ProceedingJoinPoint joinPoint,Resubmit resubmit) throws Throwable{
        //获取参数名称
        Object[] args = joinPoint.getArgs();
        //获取用户id
        Integer userId= (Integer) args[2];
        //获取方法名
        MethodSignature methodSignature= (MethodSignature) joinPoint.getSignature();
        String name = methodSignature.getMethod().getName();
        //redisson锁
        String redissonKey=name+userId;

        RLock lock=null;
        try {
            lock = redissonClient.getLock(redissonKey);
            boolean b = lock.tryLock();
            //获取锁成功，执行请求
            if (b){
                joinPoint.proceed();
            }else{
                System.out.println("重复提交请求，过滤");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }  finally {
            if(lock!=null){
                lock.unlock();
            }
        }

    }

}
