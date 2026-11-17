package com.connectly.repository;

import com.connectly.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository
        extends JpaRepository<Profile, Long> {
}