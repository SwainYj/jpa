package com.iermu.jpa.jpa;

import com.iermu.jpa.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJPA extends JpaRepository<UserEntity,Long> {
}
