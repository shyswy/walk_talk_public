package com.example.clubsite.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Retry { //외부 호출 API 같은 경우, 호출쪽에서 문제일 경우가 간혹 있다. 이경우, 단순 재조회로 해결가능.
    int value()  default 3; //기본값 3
}
