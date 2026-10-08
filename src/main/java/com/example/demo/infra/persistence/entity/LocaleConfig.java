package com.example.demo.infra.persistence.entity;

import com.example.demo.application.shared.command.outbound.CreateLocaleConfigPortCommand;
import com.example.demo.application.shared.command.outbound.UpdateLocaleConfigPortCommand;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 語系配置 Entity (充血模型)
 */
@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 規範需要無參數建構子，但不對外公開
@Table(name = "locale_config")
public class LocaleConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String uuid;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code; // 語系代碼，如 zh_tw

    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName; // 顯示名稱，如 繁體中文

    @Column(name = "enabled", nullable = false)
    private Boolean enabled; // 是否啟用

    @Column(name = "remark", columnDefinition = "nvarchar(500)")
    private String remark; // 備註

    /**
     * 建構子：用於建立全新語系
     * 
     * @param command 創建指令
     */
    public LocaleConfig(CreateLocaleConfigPortCommand command) {
        this.code = command.getCode();
        this.displayName = command.getDisplayName();
        this.enabled = command.getEnabled() != null ? command.getEnabled() : true;
        this.remark = command.getRemark();
    }

    /**
     * 處理更新語系的業務邏輯
     *
     * @param command 更新指令
     */
    public void applyUpdate(UpdateLocaleConfigPortCommand command) {
        // 在這裡可以封裝更嚴格的防呆機制，例如不允許把已經存在的代碼亂改
        this.displayName = command.getDisplayName();
        if (command.getEnabled() != null) {
            this.enabled = command.getEnabled();
        }
        this.remark = command.getRemark();
    }
}
