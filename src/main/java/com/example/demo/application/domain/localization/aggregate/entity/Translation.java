package com.example.demo.application.domain.localization.aggregate.entity;

import com.example.demo.application.domain.localization.aggregate.TranslationCategory;
import com.example.demo.application.domain.localization.command.SaveTranslateCategoryCommand;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 多語系文字 Entity
 *
 * <p>
 * 支援彈性語言擴充，每一筆對應一個語言版本。 每個 Translation 屬於一個 {@link TranslationCategory}。
 * </p>
 */
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "translation")
public class Translation {

	/**
	 * 主鍵 UUID
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String uuid;

	/**
	 * 所屬分類
	 *
	 * <p>
	 * 多對一關聯，Lazy 加載
	 * </p>
	 */
	@JsonBackReference
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id", nullable = false)
	private TranslationCategory category;

	/**
	 * 語言代碼，例如 "zh-TW", "en-US"
	 */
	@Column(name = "language", nullable = false, length = 10)
	private String language;

	/**
	 * 文字內容
	 */
	@Column(name = "text_value", nullable = false, columnDefinition = "nvarchar(max)")
	private String textValue;

	/**
	 * 備註
	 */
	@Column(name = "remark", columnDefinition = "nvarchar(max)")
	private String remark;

	/**
	 * 建立一筆多語系資料
	 *
	 * @param command  來源 Command
	 * @param category 所屬分類
	 */
	public void create(SaveTranslateCategoryCommand.SaveTranslateCommand command, TranslationCategory category) {
		this.category = category;
		this.language = command.getLanguage();
		this.textValue = command.getTextValue();
		this.remark = command.getRemark();
	}

	/**
	 * 更新文字內容與備註
	 *
	 * @param textValue 文字內容
	 * @param remark    備註
	 */
	public void updateText(String textValue, String remark) {
		this.textValue = textValue;
		this.remark = remark;
	}
}
