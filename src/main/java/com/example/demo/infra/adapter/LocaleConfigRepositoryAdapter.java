package com.example.demo.infra.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.example.demo.application.port.LocaleConfigRepositoryPort;
import com.example.demo.infra.persistence.entity.LocaleConfig;
import com.example.demo.infra.persistence.repository.LocaleConfigRepository;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class LocaleConfigRepositoryAdapter implements LocaleConfigRepositoryPort {

    private final LocaleConfigRepository repository;

    @Override
    public LocaleConfig save(LocaleConfig config) {
        return repository.save(config);
    }

    @Override
    public Optional<LocaleConfig> findByCode(String code) {
        return repository.findByCode(code);
    }

    @Override
    public List<LocaleConfig> findAll() {
        return repository.findAll();
    }
}
