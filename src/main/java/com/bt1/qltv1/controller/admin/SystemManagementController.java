package com.bt1.qltv1.controller.admin;

import com.bt1.qltv1.dto.SuccessResponseDTO;
import com.bt1.qltv1.entity.SystemConfig;
import com.bt1.qltv1.service.SystemConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/system-management")
public class SystemManagementController {
    private final SystemConfigService systemConfigService;

    @GetMapping("/config")
    public ResponseEntity<SystemConfig> getSystemConfig() {
        return ResponseEntity.ok(systemConfigService.getConfig(1L));
    }

    @PostMapping("/config/maintenance/on")
    public ResponseEntity<SuccessResponseDTO> turnOnMaintenanceMode() {

        systemConfigService.changeMaintenanceMode(true);
        return ResponseEntity.ok(new SuccessResponseDTO(HttpStatus.OK,
                "Maintenance mode is ON"));
    }

    @PostMapping("/maintenance/off")
    public ResponseEntity<SuccessResponseDTO> turnOffMaintenanceMode() {
        systemConfigService.changeMaintenanceMode(false);
        return ResponseEntity.ok(new SuccessResponseDTO(HttpStatus.OK,
                "Maintenance mode is OFF"));
    }
}
