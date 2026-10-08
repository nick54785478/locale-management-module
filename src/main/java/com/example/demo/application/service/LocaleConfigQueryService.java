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

/**
 * 語系配置查詢服務 (Application Query Service)
 *
 * <p>遵循 CQS (Command Query Separation) 原則，此服務專職處理「唯讀」的查詢操作，絕對不會改變系統狀態。
 * 負責透過 Outbound Port 取得資料，並轉換為供前端或 API 呈現的 Data Transfer Object (GottenResult)。</p>
 */
@Service
@AllArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class LocaleConfigQueryService {

    private final LocaleConfigRepositoryPort repositoryPort;

    /**
     * 取得系統內所有註冊的語系配置清單
     *
     * @return 語系配置的查詢結果 DTO 清單
     */
    public List<LocaleConfigGottenResult> findAll() {
        return repositoryPort.findAll();
    }

    /**
     * 根據語系代碼查詢特定的語系配置
     *
     * @param code 語系代碼，例如 "zh_tw"
     * @return 包含語系查詢結果的 Optional，若不存在則回傳 Optional.empty()
     */
    public Optional<LocaleConfigGottenResult> findByCode(String code) {
        return repositoryPort.findByCode(code);
    }
}
