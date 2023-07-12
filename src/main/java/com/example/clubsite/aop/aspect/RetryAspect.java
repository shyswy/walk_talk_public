package com.example.clubsite.aop.aspect;

import com.example.clubsite.aop.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
@Slf4j
public class RetryAspect {
    @Around("@annotation(retry)")
    public Object doRetry(ProceedingJoinPoint joinPoint, Retry retry) throws Throwable{//파라미터 전달로 대체된다. 따라서 포인트컷에 자동으로 어노테이션 타입이 "Retry"인 것만 들어오게 된다.
        log.info("[retry] {} retry={}",joinPoint.getSignature(),retry);
        Exception exceptionHolder=null;
        int maxTryNum = retry.value();
        for(int retryCount=1;retryCount<maxTryNum;retryCount++)
        try {
            log.info("[retry] try count={}",retryCount);
            return joinPoint.proceed();
        }catch (Exception e){
            exceptionHolder=e;
        }
        log.error("[retry]- [error trace]    \n {}",exceptionHolder);
        throw new RuntimeException(exceptionHolder); // 수정 필요, 예외가 어디로 갈지 확인
    }
}
