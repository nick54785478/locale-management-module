package com.example.demo.infra.mapper;

import java.util.Map;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.demo.application.domain.localization.aggregate.entity.Translation;
import com.example.demo.config.config.MapStructConfiguration;
import com.example.demo.infra.shared.context.ContextHolder;
import com.example.demo.infra.shared.res.BaseExceptionResponse;

@Mapper(componentModel = "spring", config = MapStructConfiguration.class)
public interface ExceptionMapper {

	/**
	 * 轉換錯誤回應
	 *
	 * @param response    錯誤回應
	 * @param translation 多語配置資料
	 * @param code        錯誤碼
	 */
	@Mapping(source = "code", target = "code")
	@Mapping(source = "translation.textValue", target = "message")
	void transform(@MappingTarget BaseExceptionResponse response, Translation translation, String code,
			Map<String, String> fields);

	/**
	 * 轉換後要執行的動作 (確保 code & message 一定不為 null)
	 * <p>
	 * 註. @AfterMapping 方法的參數必須與前面 @Mapping 方法的參數「一模一樣」才能被 MapStruct 認出並呼叫。
	 * </p>
	 *
	 * @param response    錯誤回應
	 * @param translation 多語配置資料
	 * @param code        錯誤碼
	 */
	@AfterMapping
	default void afterMapping(@MappingTarget BaseExceptionResponse response, Translation translation, String code,
			Map<String, String> fields) {

		// 如果 Code 為空，使用預設 Code
		if (response.getCode() == null) {
			response.setCode("422");
		}

		// 如果 Message 為空，使用預設 Message
		if (response.getMessage() == null) {
			switch (ContextHolder.getLang()) {
			case "zh-tw":
				response.setMessage("發生錯誤，拋出例外");
				break;
			case "zh-cn":
				response.setMessage("发生错误，抛出异常");
				break;
			case null, default:
				response.setMessage("Error Occurred, throw Exception");
			}

			// 如果 Message 不為空且 fields 不為空,則在 Message 後面中加入 fields
		} else if (!fields.isEmpty()) {
			StringBuilder sb = new StringBuilder(response.getMessage());
			fields.forEach((k, v) -> sb.append(", ").append(k).append("=").append(v));
			response.setMessage(sb.toString());
		}
	}
}
