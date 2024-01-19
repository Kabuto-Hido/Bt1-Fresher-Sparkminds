package com.bt1.qltv1.repository;

import com.bt1.qltv1.entity.SystemConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@EnableJpaRepositories
@Repository
public interface SystemConfigRepository extends JpaRepository<SystemConfig, Long> {
}
