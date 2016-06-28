package com.nowcoder.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by yby on 2016/6/28.
 */
@Aspect
@Component
public class LogAspect {
    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Before("execution(* com.nowcoder.controller.*Controller.*(..))")
    public void beforeMethod(JoinPoint joinPoint){

        StringBuilder sb = new StringBuilder();
        for (Object arg : joinPoint.getArgs()){
            sb.append("arg:" + arg + "|");
        }

        logger.info("before mess" + sb.toString());
    }
    @After("execution(* com.nowcoder.controller.*Controller.*(..))")
    public void afterMethod(JoinPoint joinPoint){

        StringBuilder sb = new StringBuilder();
        for (Object arg : joinPoint.getArgs()){
            sb.append("arg:" + arg + "|");
        }

        logger.info("after mess" + sb.toString());
    }
}
