package com.example.demo.iface.handler;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.demo.infra.locale.translator.LocaleExceptionMessageTranslator;
import com.example.demo.infra.mapper.ExceptionMapper;
import com.example.demo.infra.shared.context.ContextHolder;
import com.example.demo.infra.shared.exception.BaseLocalizableException;
import com.example.demo.infra.shared.res.BaseExceptionResponse;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 全域例外處理器（Global Exception Handler）
 *
 * <p>
 * 由於系統底層預設的 Exception Handler 輸出的 Response 格式不符合本專案 API 規範， 故定義此類別以統一處理業務例外。
 * </p>
 *
 * <p>
 * 核心職責：
 * </p>
 * <ul>
 * <li>攔截 {@link BaseLocalizableException} 及其子類</li>
 * <li>透過 {@link LocaleExceptionMessageTranslator} 進行訊息轉譯</li>
 * <li>封裝轉譯結果至 {@link BaseExceptionResponse} 並回傳</li>
 * </ul>
 */
@Slf4j
@RestControllerAdvice
@AllArgsConstructor
public class GlobalExceptionHandler {

	/**
	 * 多語系轉譯邏輯對應至 API Response 的映射器
	 */
	private ExceptionMapper exceptionMapper;

	/**
	 * 例外訊息專用的多語系轉譯器
	 */
	private LocaleExceptionMessageTranslator localeExceptionMessageTranslator;

	/**
	 * 攔截並處理業務自定義的檢核或邏輯例外。
	 *
	 * <p>
	 * 此方法會從 Context 中取得當前 Request 的語系資訊，並嘗試找出對應的錯誤訊息。 若查無對應翻譯，則會觸發 Rollback
	 * 機制回傳預設錯誤。
	 * </p>
	 *
	 * @param e 攔截到的多語系業務例外
	 * @return 封裝了錯誤碼與翻譯訊息的 ResponseEntity
	 */
	@ExceptionHandler(BaseLocalizableException.class)
	public ResponseEntity<BaseExceptionResponse> handleValidationException(BaseLocalizableException e) {
		log.info("處理檢核例外: 錯誤碼:{}, 錯誤代號:{}, 當前語系:{}", e.getCode(), e.getMessageKey(), ContextHolder.getLang());

		BaseExceptionResponse response = new BaseExceptionResponse();
		this.applyLocalizedExceptionMessage(response, e.getMessageKey(), e.getCode(), e.getFields());

		// 業務例外統一回傳 200 OK，由內部 code 區分錯誤類型（視專案規範調整）
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * 執行多語系訊息轉譯並套用至 Response 內容。
	 *
	 * @param response   準備回傳的 Response DTO
	 * @param messageKey 預計查詢的多語系 Key
	 * @param code       內部錯誤碼
	 * @param fields     動態替換參數（用於格式化訊息）
	 */
	private void applyLocalizedExceptionMessage(BaseExceptionResponse response, String messageKey, String code,
			Map<String, String> fields) {
		String lang = ContextHolder.getLang();

		// 查詢翻譯，若存在則進行轉換；若不存在則回退至 DEFAULT_ERROR
		localeExceptionMessageTranslator.getTranslation(messageKey, lang).ifPresentOrElse(
				translation -> exceptionMapper.transform(response, translation, code, fields),
				() -> rollbackDefaultExceptionMessage(response, code, fields));
	}

	/**
	 * 當指定的 messageKey 查無翻譯時的後備（Rollback）處理機制。
	 *
	 * <p>
	 * 會嘗試搜尋 "DEFAULT_ERROR" 這個系統預設 Key 的翻譯。
	 * </p>
	 *
	 * @param response {@link BaseExceptionResponse}
	 * @param code     原始錯誤碼
	 * @param fields   動態參數
	 */
	private void rollbackDefaultExceptionMessage(BaseExceptionResponse response, String code,
			Map<String, String> fields) {
		String lang = ContextHolder.getLang();
		localeExceptionMessageTranslator.getTranslation("DEFAULT_ERROR", lang)
				.ifPresent(translation -> exceptionMapper.transform(response, translation, code, fields));
	}
}