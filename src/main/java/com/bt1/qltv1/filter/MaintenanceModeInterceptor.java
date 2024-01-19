package com.bt1.qltv1.filter;


import com.bt1.qltv1.service.SystemConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Log4j
@RequiredArgsConstructor
public class MaintenanceModeInterceptor implements HandlerInterceptor {
    private final SystemConfigService systemConfigService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Boolean maintenanceMode = systemConfigService.getModeMaintenanceFromRedis(1L);
        if (Boolean.TRUE.equals(maintenanceMode)) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            response.getWriter().write("Service is currently in maintenance mode.");
            return false;
        }
        return true;
    }
}
