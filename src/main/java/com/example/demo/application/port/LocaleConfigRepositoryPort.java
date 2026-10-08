package com.example.demo.application.port;

import java.util.List;
import java.util.Optional;

import com.example.demo.application.shared.command.outbound.CreateLocaleConfigPortCommand;
import com.example.demo.application.shared.command.outbound.UpdateLocaleConfigPortCommand;
import com.example.demo.application.shared.dto.LocaleConfigGottenResult;

/**
 * 語系配置儲存庫介面 (Repository Port)
 * 
 * <p>根據六角架構 (Hexagonal Architecture) 的原則，這是一個輸出端口 (Outbound Port)。
 * Application Core 只依賴此介面進行資料庫的存取，而具體的實作 (Adapter) 則放在 Infra 層，
 * 確保業務邏輯不會與特定的資料庫框架 (如 Spring Data JPA) 產生直接耦合。</p>
 */
public interface LocaleConfigRepositoryPort {
    
    /**
     * 新增語系配置
     * 
     * @param command 要新增的語系資料
     */
    void createConfig(CreateLocaleConfigPortCommand command);

    /**
     * 更新語系配置
     * 
     * @param command 要更新的語系資料
     */
    void updateConfig(UpdateLocaleConfigPortCommand command);
    
    /**
     * 檢查語系代碼是否存在
     * 
     * @param code 語系代碼
     * @return 存在與否
     */
    boolean existsByCode(String code);

    /**
     * 根據語系代碼尋找指定的語系配置
     * 
     * @param code 語系代碼，例如 "zh_tw"
     * @return 包含語系資料的 Optional，若找不到則回傳 Optional.empty()
     */
    Optional<LocaleConfigGottenResult> findByCode(String code);
    
    /**
     * 取得系統內所有的語系配置清單
     * 
     * @return 語系資料清單
     */
    List<LocaleConfigGottenResult> findAll();
}
