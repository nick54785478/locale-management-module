package com.example.demo.infra.shared.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 驗證例外 (Validation Exception)
 *
 * <p>
 * 用於表單或業務檢核失敗時拋出的例外。 繼承自 {@link BaseLocalizableException}，支援多語言訊息與額外參數欄位。
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ValidationException extends BaseLocalizableException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 錯誤代碼 (例如: 400、422)
	 */
	private String code;

	/**
	 * 對應多語配置的 key (EXCEPTION_MESSAGE.xxx)
	 */
	private String message;

	/**
	 * 額外參數欄位
	 * <p>
	 * key: 欄位名稱<br>
	 * value: 錯誤參數（僅用於訊息組裝，不參與序列化）
	 * </p>
	 */
	private Map<String, String> fields = new LinkedHashMap<>();

	/**
	 * Constructor
	 */
	public ValidationException(String code, String message) {
		this.code = code;
		this.message = message;
	}

	/**
	 * 使用訊息建構例外
	 *
	 * @param message 對應多語 key
	 */
	protected ValidationException(String message) {
		super(message);
	}

	/**
	 * 取得多語 key
	 *
	 * @return 多語 key，例如 EXCEPTION_MESSAGE.xxx
	 */
	@Override
	public String getMessageKey() {
		return message;
	}
}
