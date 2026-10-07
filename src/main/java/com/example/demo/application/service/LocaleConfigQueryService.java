package com.example.demo.application.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.application.port.LocaleConfigRepositoryPort;
import com.example.demo.application.shared.dto.LocaleConfigGottenResult;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class LocaleConfigQueryService {

    private final LocaleConfigRepositoryPort repositoryPort;

    public List<LocaleConfigGottenResult> findAll() {
        return repositoryPort.findAll().stream()
                .map(entity -> new LocaleConfigGottenResult(entity.getCode(), entity.getDisplayName(), entity.getEnabled(), entity.getRemark()))
                .collect(Collectors.toList());
    }

    public Optional<LocaleConfigGottenResult> findByCode(String code) {
        return repositoryPort.findByCode(code)
                .map(entity -> new LocaleConfigGottenResult(entity.getCode(), entity.getDisplayName(), entity.getEnabled(), entity.getRemark()));
    }
}
