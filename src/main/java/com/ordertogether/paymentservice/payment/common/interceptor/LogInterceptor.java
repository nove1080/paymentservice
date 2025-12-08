package com.ordertogether.paymentservice.payment.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.InetAddress;
import java.net.UnknownHostException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class LogInterceptor implements HandlerInterceptor {

    private static final String START_TIME_ATTRIBUTE = "REQUEST_START_TIME";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        long startTime = System.currentTimeMillis();
        request.setAttribute(START_TIME_ATTRIBUTE, startTime);

        log.info(createHttpRequestLogMessage(request.getMethod(), request.getRequestURI(), extractClientIP(request), request.getQueryString()));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;

        String httpResponseLogMessage = createHttpResponseLogMessage(
            request.getMethod(),
            request.getRequestURI(),
            HttpStatus.valueOf(response.getStatus()).toString(),
            elapsedTime);

        log.info(httpResponseLogMessage);
    }

    private static String extractClientIP(HttpServletRequest request) throws UnknownHostException {
        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-RealIP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("REMOTE_ADDR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        if (ip.equals("0:0:0:0:0:0:0:1") || ip.equals("127.0.0.1")) {
            InetAddress address = InetAddress.getLocalHost();
            ip = address.getHostName() + "/" + address.getHostAddress();
        }

        return ip;
    }

    private static String createHttpRequestLogMessage(
        String httpMethod, String requestUri, String clientIp, String requestParam) {
        return String.format("""
        
        [REQ]
        ├─ METHOD       : %s
        ├─ URI          : %s
        ├─ CLIENT_IP    : %s
        └─ PARAMS       : %s
        """, httpMethod, requestUri, clientIp, requestParam);
    }

    private static String createHttpResponseLogMessage(
        String httpMethod, String requestUri, String httpStatus, long elapsedTime) {
        return String.format("""
        
        [RES]
        ├─ METHOD       : %s
        ├─ URI          : %s
        ├─ STATUS       : %s
        └─ ELAPSED_TIME : %d ms
        """, httpMethod, requestUri, httpStatus, elapsedTime);
    }
}
