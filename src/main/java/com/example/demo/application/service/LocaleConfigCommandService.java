package com.example.demo.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.application.port.LocaleConfigRepositoryPort;
import com.example.demo.application.shared.command.inbound.CreateLocaleConfigCommand;
import com.example.demo.application.shared.command.inbound.UpdateLocaleConfigCommand;
import com.example.demo.application.shared.exception.LocaleAlreadyExistsException;
import com.example.demo.application.shared.exception.LocaleNotFoundException;
import com.example.demo.infra.persistence.entity.LocaleConfig;

import lombok.AllArgsConstructor;

/**
 * 語系配置指令服務 (Application Command Service)
 *
 * <p>遵循 CQS (Command Query Separation) 原則，此服務專職處理「改變系統狀態」的操作 (寫入、更新)。
 * 作為 Application Core 的核心編排者，它負責接收 Inbound Command，協調 Domain Entity 進行業務邏輯驗證，
 * 最終將狀態改變交由 Outbound Port 進行持久化。不負責回傳查詢結果。</p>
 */
@Service
@AllArgsConstructor
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class LocaleConfigCommandService {

    private final LocaleConfigRepositoryPort repositoryPort;

    /**
     * 建立新的語系配置
     *
     * @param command 包含建立語系所需資料的指令 (Inbound Command)
     * @throws IllegalArgumentException 當語系已經存在時拋出例外
     */
    public void create(CreateLocaleConfigCommand command) {
        LocaleConfig entity = repositoryPort.findByCode(command.getCode()).orElse(null);
        
        if (entity != null) {
            throw new LocaleAlreadyExistsException();
        }
        
        entity = new LocaleConfig(command);
        repositoryPort.save(entity);
    }

    /**
     * 更新已存在的語系配置
     *
     * @param command 包含更新語系所需資料的指令 (Inbound Command)
     * @throws IllegalArgumentException 當語系不存在時拋出例外
     */
    public void update(UpdateLocaleConfigCommand command) {
        LocaleConfig entity = repositoryPort.findByCode(command.getCode())
            .orElseThrow(() -> new LocaleNotFoundException());
            
        entity.applyUpdate(command);
        repositoryPort.save(entity);
    }
}
