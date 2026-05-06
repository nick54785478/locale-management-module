package com.example.demo.application.domain.localization.aggregate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.example.demo.application.domain.localization.aggregate.entity.Translation;
import com.example.demo.application.domain.localization.command.SaveTranslateCategoryCommand;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 文字分類 Entity
 *
 * <p>
 * 用於管理多語系文字的分類，例如 "SYSTEM_MESSAGE", "UI_LABEL"。  
 * 每個分類可以對應多筆 {@link Translation}，支援多語言版本管理。
 * </p>
 *
 * <p>
 * Aggregate Root: TranslationCategory 是 Aggregate 的根，管理其內部 Translation 的生命週期。
 * </p>
 */
@Entity
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "translation_category")
public class TranslationCategory {

  /**
   * 主鍵 UUID
   */
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String uuid;

  /**
   * 分類種類，例如: "SYSTEM_MESSAGE", "UI_LABEL"
   */
  @Column(name = "type", nullable = false, length = 50)
  private String type;

  /**
   * 分類代碼
   *
   * <p>以 SYSTEM_MESSAGE 為例，代碼可能為 "SUCCESS"、"ERROR" 等</p>
   */
  @Column(name = "code", nullable = false, length = 50)
  private String code;

  /**
   * 分類描述
   */
  @Column(name = "description", columnDefinition = "nvarchar(max)")
  private String description;

  /**
   * 對應的多語系文字集合
   *
   * <p>使用 JsonManagedReference 避免序列化循環參考</p>
   */
  @JsonManagedReference
  @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Translation> translations = new ArrayList<>();

  /**
   * 依據 Command 狀態，決定建立或更新 Category
   *
   * @param command {@link SaveTranslateCategoryCommand}
   */
  public void apply(SaveTranslateCategoryCommand command) {
    if (this.uuid == null) {
      this.create(command);
    } else {
      this.update(command);
    }
  }

  /**
   * 新增一筆語系配置資料
   *
   * @param command {@link SaveTranslateCategoryCommand}
   */
  public void create(SaveTranslateCategoryCommand command) {
    this.type = command.getType();
    this.code = command.getCode();
    this.description = command.getDescription();

    command.getTranslations().forEach(dto -> {
      Translation translation = new Translation();
      translation.create(dto, this);
      this.translations.add(translation);
    });
  }

  /**
   * 更新一筆語系配置資料 (僅適用於前端 InlineEditable)
   *
   * <p>會自動新增、更新或移除 Translation 集合內的語言資料</p>
   *
   * @param command {@link SaveTranslateCategoryCommand}
   */
  public void update(SaveTranslateCategoryCommand command) {
    this.type = command.getType();
    this.code = command.getCode();
    this.description = command.getDescription();

    // 以 language 為 key 建索引
    Map<String, Translation> existingMap =
      this.translations.stream()
        .collect(Collectors.toMap(Translation::getLanguage, Function.identity()));

    // 更新 / 新增
    for (SaveTranslateCategoryCommand.SaveTranslateCommand dto : command.getTranslations()) {
      Translation translation = existingMap.get(dto.getLanguage());

      if (translation != null) {
        translation.updateText(dto.getTextValue(), dto.getRemark());
      } else {
        Translation newOne = new Translation();
        newOne.create(dto, this);
        this.translations.add(newOne);
      }
    }

    // 移除已不存在的語系
    Set<String> incomingLanguages =
      command.getTranslations().stream()
        .map(SaveTranslateCategoryCommand.SaveTranslateCommand::getLanguage)
        .collect(Collectors.toSet());

    this.translations.removeIf(t -> !incomingLanguages.contains(t.getLanguage()));
  }
}
