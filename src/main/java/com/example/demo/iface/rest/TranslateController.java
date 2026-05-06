package com.example.demo.iface.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.application.domain.localization.command.SaveTranslateCategoryCommand;
import com.example.demo.application.service.TranslationCommandService;
import com.example.demo.application.service.TranslationQueryService;
import com.example.demo.application.shared.TranslateCategoryQueriedData;
import com.example.demo.iface.dto.req.SaveTranslateCategoryResource;
import com.example.demo.iface.dto.res.TranslateCategoryQueriedResource;
import com.example.demo.iface.dto.res.TranslateCategorySavedResource;
import com.example.demo.infra.mapper.TranslationMapper;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/translation")
public class TranslateController {

	private TranslationMapper mapper;
	private TranslationCommandService translationCommandService;
	private TranslationQueryService translationQueryService;

	/**
	 * 新增一筆 多語系配置
	 */
	@PostMapping("")
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
	public ResponseEntity<TranslateCategoryQueriedResource> getTranslateCategory(@RequestParam String type,
			@RequestParam String code) {
		TranslateCategoryQueriedData category = translationQueryService.getCategory(type, code);
		return ResponseEntity.ok(new TranslateCategoryQueriedResource("200", "SUCCESS", category));
	}
	
}
