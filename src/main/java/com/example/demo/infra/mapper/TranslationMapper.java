package com.example.demo.infra.mapper;

import org.mapstruct.Mapper;

import com.example.demo.infra.persistence.entity.TranslationCategory;
import com.example.demo.infra.persistence.entity.Translation;
import com.example.demo.application.shared.command.SaveTranslateCategoryCommand;
import com.example.demo.application.shared.dto.TranslateCategoryQueriedData;
import com.example.demo.config.config.MapStructConfiguration;
import com.example.demo.iface.dto.req.SaveTranslateCategoryResource;
import com.example.demo.application.shared.payload.TranslationChangedPayload;

@Mapper(componentModel = "spring", config = MapStructConfiguration.class)
public interface TranslationMapper {

	SaveTranslateCategoryCommand transform(SaveTranslateCategoryResource resource);

	SaveTranslateCategoryCommand.SaveTranslateCommand transform(
			SaveTranslateCategoryResource.SaveTranslateResource resource);

	TranslateCategoryQueriedData transformToQueriedData(TranslationCategory category);

	TranslateCategoryQueriedData.TranslateQueriedData transform(Translation translation);

	TranslationChangedPayload transformToPayload(Translation translation);
}
