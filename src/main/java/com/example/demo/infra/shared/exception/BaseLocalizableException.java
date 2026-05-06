package com.example.demo.infra.shared.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Getter;

/**
 * 基底可本地化例外 (Base Localizable Exception)
 *
 * <p>
 * 用於需要多語言支援的業務例外類別。 包含業務錯誤碼、對應多語言 key 以及可選的額外參數欄位。
 * </p>
 *
 * <p>
 * fields 為 transient，僅用於訊息組裝，不會被序列化傳輸。
 * </p>
 */
@Getter
public abstract class BaseLocalizableException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 補充欄位（可選）
	 * <p>
	 * key: 欄位名稱<br>
	 * value: 錯誤參數（僅用於訊息組裝，不參與序列化）
	 * </p>
	 */
	protected final transient Map<String, String> fields = new LinkedHashMap<>();

	/**
	 * 預設建構子
	 */
	protected BaseLocalizableException() {
		super();
	}

	/**
	 * 使用 debug 訊息的建構子
	 *
	 * @param debugMessage 附加的調試訊息
	 */
	protected BaseLocalizableException(String debugMessage) {
		super(debugMessage);
	}

	/**
	 * 取得業務錯誤碼
	 *
	 * @return 錯誤碼 (如 HTTP 狀態碼或自訂錯誤碼)
	 */
	public abstract String getCode();

	/**
	 * 取得多語 key
	 *
	 * @return 對應的多語 key，例如 EXCEPTION_MESSAGE.xxx
	 */
	public abstract String getMessageKey();

}
