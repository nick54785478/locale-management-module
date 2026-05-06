package com.example.demo.infra.mapper;

import org.mapstruct.Mapper;

import com.example.demo.application.domain.localization.aggregate.TranslationCategory;
import com.example.demo.application.domain.localization.aggregate.entity.Translation;
import com.example.demo.application.domain.localization.command.SaveTranslateCategoryCommand;
import com.example.demo.application.shared.TranslateCategoryQueriedData;
import com.example.demo.config.config.MapStructConfiguration;
import com.example.demo.iface.dto.req.SaveTranslateCategoryResource;
import com.example.demo.infra.locale.share.payload.TranslationChangedPayload;

@Mapper(componentModel = "spring", config = MapStructConfiguration.class)
public interface TranslationMapper {

	SaveTranslateCategoryCommand transform(SaveTranslateCategoryResource resource);

	SaveTranslateCategoryCommand.SaveTranslateCommand transform(
			SaveTranslateCategoryResource.SaveTranslateResource resource);

	TranslateCategoryQueriedData transformAggregate(TranslationCategory category);

	TranslateCategoryQueriedData.TranslateQueriedData transform(Translation translation);

	TranslationChangedPayload transformToPayload(Translation translation);
}
