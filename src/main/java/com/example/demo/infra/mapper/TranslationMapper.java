package com.example.demo.infra.mapper;

import org.mapstruct.Mapper;

import com.example.demo.infra.persistence.entity.TranslationCategory;
import com.example.demo.infra.persistence.entity.Translation;
import com.example.demo.application.shared.command.inbound.CreateTranslateCategoryCommand;
import com.example.demo.application.shared.command.inbound.UpdateTranslateCategoryCommand;
import com.example.demo.application.shared.dto.TranslateCategoryGottenResult;
import com.example.demo.config.config.MapStructConfiguration;
import com.example.demo.iface.dto.req.CreateTranslateCategoryResource;
import com.example.demo.iface.dto.req.UpdateTranslateCategoryResource;
import com.example.demo.application.shared.payload.TranslationChangedPayload;

@Mapper(componentModel = "spring", config = MapStructConfiguration.class)
public interface TranslationMapper {

	CreateTranslateCategoryCommand transform(CreateTranslateCategoryResource resource);

	CreateTranslateCategoryCommand.CreateTranslateCommand transform(
			CreateTranslateCategoryResource.CreateTranslateResource resource);

	UpdateTranslateCategoryCommand transform(UpdateTranslateCategoryResource resource);

	UpdateTranslateCategoryCommand.UpdateTranslateCommand transform(
			UpdateTranslateCategoryResource.UpdateTranslateResource resource);

	TranslateCategoryGottenResult transformToQueriedData(TranslationCategory category);

	TranslateCategoryGottenResult.TranslateGottenResult transform(Translation translation);

	TranslationChangedPayload transformToPayload(Translation translation);
}
