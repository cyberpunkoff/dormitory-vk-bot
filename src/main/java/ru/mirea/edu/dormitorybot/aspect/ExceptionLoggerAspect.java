package ru.mirea.edu.dormitorybot.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ExceptionLoggerAspect {
    @Around("bean(*Action)")
    public Object aroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (Throwable ex) {
            log.error(
                    "Exception caught during method execution \n Cause: {}, Message: {}",
                    ex.getCause(),
                    ex.getMessage()
            );
            throw new RuntimeException();
        }
    }
}
