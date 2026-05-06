package com.example.demo.application.shared;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TranslateCategoryQueriedData {

	private String uuid;
	private String type;
	private String code;
	private String description;
	private List<TranslateQueriedData> translations = new ArrayList<>();

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class TranslateQueriedData {
		private String language;
		private String textValue;
		private String remark;
	}
}
