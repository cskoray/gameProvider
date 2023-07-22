package com.solidcode.gameprovider.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AppMvcInterceptor implements HandlerInterceptor {

  private final String TRACE_HEADER = "X-Trace-Id";

  public AppMvcInterceptor() {
    MDC.put(TRACE_HEADER, UUID.randomUUID().toString());
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    String traceID = UUID.randomUUID().toString();
    MDC.put(TRACE_HEADER, traceID);
    return true;
  }
}