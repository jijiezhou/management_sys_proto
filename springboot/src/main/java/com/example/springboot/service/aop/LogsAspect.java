package com.example.springboot.service.aop;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ArrayUtil;
import com.example.springboot.common.SysLogs;
import com.example.springboot.entity.Logs;
import com.example.springboot.entity.User;
import com.example.springboot.service.LogsService;
import com.example.springboot.utils.IpUtils;
import com.example.springboot.utils.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Component
@Aspect
@Slf4j
public class LogsAspect {

    @Resource
    LogsService logsService;

    /**
     * operation logs
     * @param joinPoint
     * @param sysLogs
     * @param jsonResult
     */
    @AfterReturning(pointcut = "@annotation(sysLogs)", returning = "jsonResult")
    public void recordLog(JoinPoint joinPoint, SysLogs sysLogs, Object jsonResult) {
        // already login: get current login user info
        User loginUser = TokenUtils.getCurrentUser();
        // not login -> loginUser is null, we need get user from params
        if (loginUser == null) {
            // login/ register
            Object[] args = joinPoint.getArgs();
            if (ArrayUtil.isNotEmpty(args)) {
                if (args[0] instanceof User) {
                    loginUser = (User) args[0];
                }
            }
        }
        //if both above cannot get user
        if (loginUser == null) {
            log.error("The current operating user information is not obtained. Log Error!");
            return;
        }
        // Get HttpServletRequest Object
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        // get IP info
        String ipAddr = IpUtils.getIpAddr(request);
        // Assembling Logs entity
        Logs logs = Logs.builder()
                .user(loginUser.getUsername())
                .operation(sysLogs.operation())
                .type(sysLogs.type().getValue())
                .ip(ipAddr)
                .time(DateUtil.now())
                .build();

        // Insert to database
        ThreadUtil.execAsync(() -> {
            logsService.save(logs);
        });
    }
}
