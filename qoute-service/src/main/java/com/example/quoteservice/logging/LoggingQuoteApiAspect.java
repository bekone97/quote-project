package com.example.quoteservice.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingQuoteApiAspect {

    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void quoteApiServiceBean(){}

    @Pointcut("within(com.example.quoteservice.service..*)")
    public void quoteApiServicePackage(){}

    @Pointcut("within(com.example.quoteservice.controller..*)")
    public void quoteApiControllerPackage(){}

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void quoteApiControllerBean(){}

    @Pointcut("within(com.example.quoteservice..*)")
    public void quoteApiExceptionPackage(){}

    @Pointcut("!target(org.springframework.web.filter.GenericFilterBean)")
    public void quoteApiExcludeGenericFilterBean(){}

    @Around("quoteApiServiceBean() && quoteApiServicePackage() && quoteApiExcludeGenericFilterBean()")
    public Object logAroundService(ProceedingJoinPoint joinPoint) throws Throwable {
        if (log.isDebugEnabled())
            log.debug("Request for {}.{}() with arguments[s]={}",joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));
        Object returnValue = joinPoint.proceed();
        if (log.isDebugEnabled() && returnValue!=null){
            log.debug("Response for {}.{} Result = {}", joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(), returnValue);
        }
        return returnValue;
    }

    @Around("quoteApiControllerBean() && quoteApiControllerPackage() && quoteApiExcludeGenericFilterBean()")
    public Object logAroundController(ProceedingJoinPoint joinPoint) throws Throwable{
        log.info("Request for {}.{}() with arguments[s]={}",joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),Arrays.toString(joinPoint.getArgs()));

        var returnValue =joinPoint.proceed();

        log.info("Response for {}.{} ", joinPoint.getSignature().getDeclaringTypeName()
                ,joinPoint.getSignature().getName());
        return returnValue;
    }

    @AfterThrowing(pointcut = "quoteApiExceptionPackage() && quoteApiExcludeGenericFilterBean()",throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception){
        log.error("Exception in {}.{} with message = {}",
                joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(),
                exception.getMessage()!=null?exception.getMessage():"null message");
    }
}
