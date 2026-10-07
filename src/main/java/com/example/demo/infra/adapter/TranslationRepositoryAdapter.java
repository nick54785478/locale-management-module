package com.example.demo.infra.adapter;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.ArrayList;

import com.example.demo.application.shared.dto.TranslateCategoryGottenResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.infra.persistence.entity.TranslationCategory;
import com.example.demo.application.port.TranslationRepositoryPort;
import com.example.demo.application.shared.command.outbound.CreateTranslateCategoryPortCommand;
import com.example.demo.application.shared.command.outbound.UpdateTranslateCategoryPortCommand;
import com.example.demo.application.shared.dto.PagedGottenResult;
import com.example.demo.application.shared.payload.TranslationChangedPayload;
import com.example.demo.infra.persistence.repository.TranslationCategoryRepository;
import com.example.demo.infra.spec.GetTranslationSpecification;
import com.example.demo.infra.mapper.TranslationMapper;

import lombok.AllArgsConstructor;

/**
 * Repository Port 的基礎設施實作（Adapter）
 * 
 * <p>負責將 Application Layer 要求的持久化操作轉換為 Spring Data JPA 的具體實作，並將實體轉換為 DTO。</p>
 */
@Component
@AllArgsConstructor
public class TranslationRepositoryAdapter implements TranslationRepositoryPort {

	private final TranslationCategoryRepository repository;
	private final TranslationMapper mapper;

	@Override
	public TranslateCategoryGottenResult getCategory(String type, String code) {
		TranslationCategory translationCategory = repository.findByTypeAndCode(type, code)
				.orElseGet(TranslationCategory::new);
		return mapper.transformToQueriedData(translationCategory);
	}

	@Override
	public PagedGottenResult<TranslateCategoryGottenResult> getPagedCategories(String type, String code, Integer page, Integer size) {
		GetTranslationSpecification specification = new GetTranslationSpecification(type, code);
		PageRequest pageRequest = PageRequest.of(page, size);
		Page<TranslationCategory> pagedData = repository.findAll(specification, pageRequest);

		List<TranslateCategoryGottenResult> content = pagedData.getContent().stream()
				.map(mapper::transformToQueriedData)
				.toList();

		return new PagedGottenResult<>(
				content,
				pagedData.getTotalElements(),
				pagedData.getTotalPages(),
				pagedData.getNumber(),
				pagedData.getSize()
		);
	}

	@Override
	@Transactional
	public List<TranslationChangedPayload> createCategory(CreateTranslateCategoryPortCommand command) {
		TranslationCategory category = repository.findByTypeAndCode(command.getType(), command.getCode())
				.orElseGet(TranslationCategory::new);
		
		category.applyCreate(command);
		TranslationCategory saved = repository.save(category);
		
		return saved.getTranslations().stream()
				.map(mapper::transformToPayload)
				.toList();
	}

	@Override
	@Transactional
	public List<TranslationChangedPayload> updateCategory(UpdateTranslateCategoryPortCommand command) {
		TranslationCategory category = repository.findByTypeAndCode(command.getType(), command.getCode())
				.orElseGet(TranslationCategory::new);
		
		category.applyUpdate(command);
		TranslationCategory saved = repository.save(category);
		
		return saved.getTranslations().stream()
				.map(mapper::transformToPayload)
				.toList();
	}

	@Override
	@Transactional
	public void createCategoryList(List<CreateTranslateCategoryPortCommand> commands) {
		Set<String> types = commands.stream().map(CreateTranslateCategoryPortCommand::getType).collect(Collectors.toSet());
		Set<String> codes = commands.stream().map(CreateTranslateCategoryPortCommand::getCode).collect(Collectors.toSet());

		List<TranslationCategory> existingCategories = repository.findByTypeInAndCodeIn(types, codes);
		Map<String, TranslationCategory> categoryMap = existingCategories.stream()
				.collect(Collectors.toMap(c -> c.getType() + "-" + c.getCode(), Function.identity()));

		List<TranslationCategory> saveList = new ArrayList<>();
		for (CreateTranslateCategoryPortCommand command : commands) {
			String key = command.getType() + "-" + command.getCode();
			TranslationCategory category = categoryMap.getOrDefault(key, new TranslationCategory());
			category.applyCreate(command);
			saveList.add(category);
		}
		
		repository.saveAll(saveList);
	}
}
