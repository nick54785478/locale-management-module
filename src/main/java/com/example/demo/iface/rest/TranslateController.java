package com.example.demo.iface.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.application.shared.command.SaveTranslateCategoryCommand;
import com.example.demo.application.service.TranslationCommandService;
import com.example.demo.application.service.TranslationQueryService;
import com.example.demo.application.shared.dto.TranslateCategoryQueriedData;
import com.example.demo.iface.dto.req.SaveTranslateCategoryResource;
import com.example.demo.iface.dto.res.TranslateCategoryQueriedResource;
import com.example.demo.iface.dto.res.TranslateCategorySavedResource;
import com.example.demo.infra.mapper.TranslationMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/translation")
@Tag(name = "Translation API", description = "多語系配置管理 API")
public class TranslateController {

	private TranslationMapper mapper;
	private TranslationCommandService translationCommandService;
	private TranslationQueryService translationQueryService;

	/**
	 * 新增一筆 多語系配置
	 */
	@PostMapping("")
	@Operation(summary = "新增多語系配置", description = "新增一筆或多筆多語系配置資料")
	public ResponseEntity<TranslateCategorySavedResource> saveTranslateCategory(
			@RequestBody SaveTranslateCategoryResource resource) {
		SaveTranslateCategoryCommand command = mapper.transform(resource);
		translationCommandService.saveTranslateCategory(command);
		return ResponseEntity.ok(new TranslateCategorySavedResource("200", "SUCCESS"));
	}

	/**
	 * 取得特定的 多語系配置
	 */
	@GetMapping("")
	@Operation(summary = "取得特定的多語系配置", description = "依據 type 與 code 取得對應的多語系配置資料")
	public ResponseEntity<TranslateCategoryQueriedResource> getTranslateCategory(
			@Parameter(description = "分類類型") @RequestParam String type,
			@Parameter(description = "代碼") @RequestParam String code) {
		TranslateCategoryQueriedData category = translationQueryService.getCategory(type, code);
		return ResponseEntity.ok(new TranslateCategoryQueriedResource("200", "SUCCESS", category));
	}
	
}
