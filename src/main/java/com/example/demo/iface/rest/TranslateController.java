package com.example.demo.iface.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.application.shared.command.inbound.CreateTranslateCategoryCommand;
import com.example.demo.application.shared.command.inbound.UpdateTranslateCategoryCommand;
import com.example.demo.application.service.TranslationCommandService;
import com.example.demo.application.service.TranslationQueryService;
import com.example.demo.application.shared.dto.TranslateCategoryGottenResult;
import com.example.demo.iface.dto.req.CreateTranslateCategoryResource;
import com.example.demo.iface.dto.req.UpdateTranslateCategoryResource;
import com.example.demo.iface.dto.res.TranslateCategoryGottenResource;
import com.example.demo.iface.dto.res.TranslateCategoryCreatedResource;
import com.example.demo.iface.dto.res.TranslateCategoryUpdatedResource;
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
	public ResponseEntity<TranslateCategoryCreatedResource> createTranslateCategory(
			@RequestBody CreateTranslateCategoryResource resource) {
		CreateTranslateCategoryCommand command = mapper.transform(resource);
		translationCommandService.createTranslateCategory(command);
		return ResponseEntity.ok(new TranslateCategoryCreatedResource("200", "SUCCESS"));
	}

	/**
	 * 更新一筆 多語系配置
	 */
	@PutMapping("")
	@Operation(summary = "更新多語系配置", description = "更新已存在的多語系配置資料")
	public ResponseEntity<TranslateCategoryUpdatedResource> updateTranslateCategory(
			@RequestBody UpdateTranslateCategoryResource resource) {
		UpdateTranslateCategoryCommand command = mapper.transform(resource);
		translationCommandService.updateTranslateCategory(command);
		return ResponseEntity.ok(new TranslateCategoryUpdatedResource("200", "SUCCESS"));
	}

	/**
	 * 取得特定的 多語系配置
	 */
	@GetMapping("")
	@Operation(summary = "取得特定的多語系配置", description = "依據 type 與 code 取得對應的多語系配置資料")
	public ResponseEntity<TranslateCategoryGottenResource> getTranslateCategory(
			@Parameter(description = "分類類型") @RequestParam String type,
			@Parameter(description = "代碼") @RequestParam String code) {
		TranslateCategoryGottenResult category = translationQueryService.getCategory(type, code);
		return ResponseEntity.ok(new TranslateCategoryGottenResource("200", "SUCCESS", category));
	}
	
}
