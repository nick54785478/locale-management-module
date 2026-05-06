package com.example.demo.iface.handler;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.example.demo.application.domain.localization.aggregate.entity.Translation;
import com.example.demo.infra.locale.translator.LocaleSuccessMessageTranslator;
import com.example.demo.infra.shared.context.ContextHolder;

import lombok.extern.slf4j.Slf4j;

/**
 * 全域多語系成功訊息攔截器（Response Body Advice）
 *
 * <p>
 * 本元件攔截所有 Controller 的回傳值。若回傳物件中包含名為 "message" 的欄位， 會自動將該欄位（代碼）替換為目前語系對應的翻譯文字。
 * </p>
 *
 * <p>
 * 設計特性：
 * </p>
 * <ul>
 * <li>支援 Java Record：由於 Record 為 Immutable，會透過反射重新建構 Instance</li>
 * <li>支援標準 POJO：透過 Java Bean Getter/Setter 存取訊息欄位</li>
 * <li>無感轉譯：開發人員在 Controller 僅需回傳 Message Code（如 "SUCCESS_CREATED"）</li>
 * </ul>
 */
@Slf4j
@RestControllerAdvice
public class GlobalLocaleResponseHandler implements ResponseBodyAdvice<Object> {

	/** 成功訊息專用的多語系轉譯器 */
	@Autowired
	private LocaleSuccessMessageTranslator successMessageTranslator;

	/**
	 * 判斷哪些 Response 需要進入攔截處理。
	 * 
	 * @return true 表示攔截所有 Controller 的回應
	 */
	@Override
	public boolean supports(MethodParameter returnType, @SuppressWarnings("rawtypes") Class converterType) {
		return true;
	}

	/**
	 * 在 Body 寫入輸出流之前的最後修改機會。
	 * 
	 * @param body                  Controller 實際回傳的物件（可能是 DTO 或 ResponseEntity）
	 * @param returnType            回傳型別 metadata
	 * @param selectedContentType   內容類型
	 * @param selectedConverterType 使用的轉換器
	 * @param request               當前請求
	 * @param response              當前回應頭
	 * @return 經過多語系替換後的物件
	 */
	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
			@SuppressWarnings("rawtypes") Class selectedConverterType, ServerHttpRequest request,
			ServerHttpResponse response) {

		// 取得目前語系（ContextHolder 由 Interceptor 預先寫入）
		String lang = ContextHolder.getLang() == null ? "en_us" : ContextHolder.getLang();

		// 1. 處理 ResponseEntity 封裝情況
		Object actualBody = this.unwrapBody(body);
		if (actualBody == null) {
			return body;
		}

		try {
			// 2. 依照類型進行分流處理（Record 或 POJO）
			Object processed = actualBody.getClass().isRecord() ? this.handleRecord(actualBody, lang)
					: this.handlePojo(actualBody, lang);

			// 3. 若原本有 ResponseEntity 封裝，則需重新包裝回去
			return this.rewrapBody(body, processed);
		} catch (Exception e) {
			log.error("Failed to translate response message for body type: {}", actualBody.getClass().getName(), e);
			return body;
		}
	}

	/**
	 * 將 ResponseEntity 中的實際 Body 取出。
	 */
	private Object unwrapBody(Object body) {
		return body instanceof ResponseEntity<?> responseEntity ? responseEntity.getBody() : body;
	}

	/**
	 * 若原始資料被 ResponseEntity 封裝，處理完 Body 後須將其與原始狀態碼、Headers 重新組裝。
	 */
	private Object rewrapBody(Object original, Object newBody) {
		if (original instanceof ResponseEntity<?> responseEntity) {
			return ResponseEntity.status(responseEntity.getStatusCode()).headers(responseEntity.getHeaders())
					.body(newBody);
		}
		return newBody;
	}

	/**
	 * 處理 Java Record 類型的轉譯邏輯。
	 * <p>
	 * 利用反射讀取組件，若發現 message 則進行翻譯，並調用 Canonical Constructor 產生新實例。
	 * </p>
	 * 
	 * @param recordObj 原始 Record 物件
	 * @param lang      目標語系
	 * @return 翻譯後的新 Record 或原 Record
	 */
	private Object handleRecord(Object recordObj, String lang)
			throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
		RecordComponent[] components = recordObj.getClass().getRecordComponents();
		Map<String, Object> values = new HashMap<>();
		boolean messageChanged = false;

		for (RecordComponent component : components) {
			Object value = component.getAccessor().invoke(recordObj);
			// 尋找名為 message 且為 String 的欄位
			if ("message".equals(component.getName()) && value instanceof String msg) {
				String translated = this.translateMessage(msg, lang);
				values.put("message", translated);
				messageChanged = !translated.equals(msg);
			} else {
				values.put(component.getName(), value);
			}
		}
		// 若訊息內容有變動才重建，優化效能
		return messageChanged ? this.buildRecord(recordObj.getClass(), values) : recordObj;
	}

	/**
	 * 處理一般 POJO 類型的轉譯邏輯。
	 * <p>
	 * 遵循 Java Bean 規範，透過 getMessage 與 setMessage 存取。此舉可避免破壞封裝並符合安全檢核。
	 * </p>
	 * 
	 * @param pojo 原始 POJO 物件
	 * @param lang 目標語系
	 * @return 處理後的物件（In-place 修改）
	 */
	private Object handlePojo(Object pojo, String lang) {
		try {
			Method getter = pojo.getClass().getMethod("getMessage");
			Method setter = pojo.getClass().getMethod("setMessage", String.class);
			Object value = getter.invoke(pojo);

			if (value instanceof String msg) {
				String translated = translateMessage(msg, lang);
				// 僅在翻譯後文字不同時才調用 Setter，避免不必要的反射開銷
				if (!translated.equals(msg)) {
					setter.invoke(pojo, translated);
				}
			}
		} catch (NoSuchMethodException ignored) {
			// 若物件無標準 message getter/setter 則視為不需處理
		} catch (Exception e) {
			log.warn("Failed to handle POJO message translation", e);
		}
		return pojo;
	}

	/**
	 * 呼叫轉譯器進行訊息轉換。
	 * 
	 * @param messageCode 訊息代碼（Key）
	 * @param lang        目標語系
	 * @return 翻譯後的文字；若查無翻譯則回傳原始代碼以供除錯
	 */
	private String translateMessage(String messageCode, String lang) {
		return successMessageTranslator.getTranslation(messageCode, lang).map(Translation::getTextValue)
				.orElse(messageCode);
	}

	/**
	 * 動態構造 Record 實例。
	 * 
	 * @param recordClass Record 的 Class
	 * @param values      欄位名與值的對應 Map
	 * @return 新的 Record 實例
	 */
	private Object buildRecord(Class<?> recordClass, Map<String, Object> values)
			throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
		RecordComponent[] components = recordClass.getRecordComponents();
		Object[] args = new Object[components.length];
		for (int i = 0; i < components.length; i++) {
			args[i] = values.get(components[i].getName());
		}
		// 取得全參數建構子 (Canonical Constructor)
		Constructor<?> ctor = recordClass
				.getDeclaredConstructor(Arrays.stream(components).map(RecordComponent::getType).toArray(Class[]::new));
		return ctor.newInstance(args);
	}
}