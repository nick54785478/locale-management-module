package com.example.demo.infra.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.infra.persistence.entity.LocaleConfig;

@Repository
public interface LocaleConfigRepository extends JpaRepository<LocaleConfig, String> {
    Optional<LocaleConfig> findByCode(String code);
}
