package com.example.demo.application.shared.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TranslateCategoryGottenResult {

	private String uuid;
	private String type;
	private String code;
	private String description;
	private List<TranslateGottenResult> translations = new ArrayList<>();

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class TranslateGottenResult {
		private String language;
		private String textValue;
		private String remark;
	}
}
