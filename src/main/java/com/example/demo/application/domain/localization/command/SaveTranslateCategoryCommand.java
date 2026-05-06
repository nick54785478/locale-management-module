package com.example.demo.application.domain.localization.command;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveTranslateCategoryCommand {

	private String type; // 分類類型

	private String code; // 分類代碼

	private String description; // 分類描述

	private List<SaveTranslateCommand> translations = new ArrayList<>();

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SaveTranslateCommand {

		private String language; // 語言代碼

		private String textValue; // 文字內容

		private String remark; // 備註
	}
}
