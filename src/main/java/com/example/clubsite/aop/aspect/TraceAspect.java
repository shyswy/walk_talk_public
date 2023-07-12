package com.example.clubsite.aop.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
@Slf4j
public class TraceAspect {
    @Before("@annotation(com.example.clubsite.aop.annotation.LogTrace)")
    public void doLogTrace(JoinPoint joinPoint){
        Object args=joinPoint.getArgs();
        log.info("[trace] {} args={}",joinPoint.getSignature(),args);

    }
}
