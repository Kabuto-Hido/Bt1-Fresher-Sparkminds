package com.bt1.qltv1.service;

import com.bt1.qltv1.entity.SystemConfig;
import org.springframework.stereotype.Service;

@Service
public interface SystemConfigService {
    SystemConfig getConfig(Long id);
    Boolean getModeMaintenanceFromRedis(Long id);
    void changeMaintenanceMode(boolean b);
}
