package com.example.demo.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.application.port.LocaleConfigRepositoryPort;
import com.example.demo.application.shared.command.inbound.CreateLocaleConfigCommand;
import com.example.demo.application.shared.command.inbound.UpdateLocaleConfigCommand;
import com.example.demo.infra.persistence.entity.LocaleConfig;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class LocaleConfigCommandService {

    private final LocaleConfigRepositoryPort repositoryPort;

    public void create(CreateLocaleConfigCommand command) {
        LocaleConfig entity = repositoryPort.findByCode(command.getCode()).orElse(null);
        
        if (entity != null) {
            throw new IllegalArgumentException("語系已存在");
        }
        
        entity = new LocaleConfig(command);
        repositoryPort.save(entity);
    }

    public void update(UpdateLocaleConfigCommand command) {
        LocaleConfig entity = repositoryPort.findByCode(command.getCode())
            .orElseThrow(() -> new IllegalArgumentException("語系不存在"));
            
        entity.applyUpdate(command);
        repositoryPort.save(entity);
    }
}
