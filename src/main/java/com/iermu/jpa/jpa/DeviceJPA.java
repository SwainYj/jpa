package com.iermu.jpa.jpa;

import com.iermu.jpa.entity.DeviceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceJPA extends JpaRepository<DeviceEntity,Long> {
}
