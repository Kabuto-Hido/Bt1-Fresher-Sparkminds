package com.bt1.qltv1.service.impl;

import com.bt1.qltv1.entity.SystemConfig;
import com.bt1.qltv1.exception.NotFoundException;
import com.bt1.qltv1.repository.SystemConfigRepository;
import com.bt1.qltv1.service.SystemConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;

@Component
@Log4j
@RequiredArgsConstructor
public class SystemConfigServiceImpl implements SystemConfigService {
    private final SystemConfigRepository systemConfigRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public SystemConfig getConfig(Long id) {
        SystemConfig systemConfig = systemConfigRepository.findById(id).orElseThrow(()->
                new NotFoundException("Not found system config", "system-config.not-found"));

        saveSystemConfigToRedis(systemConfig);
        return systemConfig;
    }

    public void  saveSystemConfigToRedis(SystemConfig systemConfig) {
        redisTemplate.opsForValue().set("systemConfig:" + systemConfig.getId(), systemConfig);
    }

    @Override
    public Boolean getModeMaintenanceFromRedis(Long id) {
        Object object = redisTemplate.opsForValue().get("systemConfig:" + id);
        if (object == null) {
            throw new NotFoundException("Error get system config in redis",
                    "system-config.redis.not-found");
        }

        log.debug(object.toString());
        LinkedHashMap<String, Object> systemConfigLinkHM;
        if (!(object instanceof LinkedHashMap<?, ?>)) {
            throw new NotFoundException("An error occur",
                    "system-config.redis.error");
        }
        systemConfigLinkHM = (LinkedHashMap<String, Object>) object;
        return (Boolean) systemConfigLinkHM.get("modeMaintenance");
    }

    @Override
    public void changeMaintenanceMode(boolean b) {
        SystemConfig systemConfig = systemConfigRepository.findById(1L).orElseThrow(()->
                new NotFoundException("Not found system config", "system-config.not-found"));

        systemConfig.setModeMaintenance(b);
        systemConfigRepository.save(systemConfig);

        //update into redis
        saveSystemConfigToRedis(systemConfig);
    }
}
