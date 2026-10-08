package com.example.demo.infra.adapter;

import java.util.List;
import java.util.Optional;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.demo.application.port.LocaleConfigRepositoryPort;
import com.example.demo.application.shared.command.outbound.CreateLocaleConfigPortCommand;
import com.example.demo.application.shared.command.outbound.UpdateLocaleConfigPortCommand;
import com.example.demo.application.shared.dto.LocaleConfigGottenResult;
import com.example.demo.application.shared.exception.LocaleNotFoundException;
import com.example.demo.infra.persistence.entity.LocaleConfig;
import com.example.demo.infra.persistence.repository.LocaleConfigRepository;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class LocaleConfigRepositoryAdapter implements LocaleConfigRepositoryPort {

    private final LocaleConfigRepository repository;

    @Override
    public void createConfig(CreateLocaleConfigPortCommand command) {
        LocaleConfig entity = new LocaleConfig(command);
        repository.save(entity);
    }

    @Override
    public void updateConfig(UpdateLocaleConfigPortCommand command) {
        LocaleConfig entity = repository.findByCode(command.getCode())
            .orElseThrow(() -> new LocaleNotFoundException());
        entity.applyUpdate(command);
        repository.save(entity);
    }

    @Override
    public boolean existsByCode(String code) {
        return repository.findByCode(code).isPresent();
    }

    @Override
    public Optional<LocaleConfigGottenResult> findByCode(String code) {
        return repository.findByCode(code)
                .map(this::toResult);
    }

    @Override
    public List<LocaleConfigGottenResult> findAll() {
        return repository.findAll().stream()
                .map(this::toResult)
                .collect(Collectors.toList());
    }

    private LocaleConfigGottenResult toResult(LocaleConfig entity) {
        return new LocaleConfigGottenResult(
            entity.getCode(),
            entity.getDisplayName(),
            entity.getEnabled(),
            entity.getRemark()
        );
    }
}
