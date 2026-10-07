package com.example.demo.application.port;

import java.util.List;
import java.util.Optional;
import com.example.demo.infra.persistence.entity.LocaleConfig;

/**
 * 語系配置儲存庫介面 (Repository Port)
 * 
 * <p>根據六角架構 (Hexagonal Architecture) 的原則，這是一個輸出端口 (Outbound Port)。
 * Application Core 只依賴此介面進行資料庫的存取，而具體的實作 (Adapter) 則放在 Infra 層，
 * 確保業務邏輯不會與特定的資料庫框架 (如 Spring Data JPA) 產生直接耦合。</p>
 */
public interface LocaleConfigRepositoryPort {
    
    /**
     * 儲存或更新語系配置
     * 
     * @param config 要儲存的語系實體 (充血模型)
     * @return 儲存後的語系實體
     */
    LocaleConfig save(LocaleConfig config);
    
    /**
     * 根據語系代碼尋找指定的語系配置
     * 
     * @param code 語系代碼，例如 "zh_tw"
     * @return 包含語系實體的 Optional，若找不到則回傳 Optional.empty()
     */
    Optional<LocaleConfig> findByCode(String code);
    
    /**
     * 取得系統內所有的語系配置清單
     * 
     * @return 語系實體清單
     */
    List<LocaleConfig> findAll();
}
