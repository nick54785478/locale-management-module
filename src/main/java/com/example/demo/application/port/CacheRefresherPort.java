package com.example.demo.application.port;

import com.example.demo.application.shared.payload.TranslationChangedPayload;

/**
 * 快取刷新器 Port 定義
 *
 * <p>提供 Application Layer 刷新快取的抽象介面，解耦對 Infra 層 Payload 的依賴。</p>
 */
public interface CacheRefresherPort {

	/**
	 * 刷新快取
	 * 
	 * @param messageKey 訊息的 Key (如 code)
	 * @param lang 語言代碼
	 * @param payload 欲寫入快取的最新資料
	 */
	void refreshCache(String messageKey, String lang, TranslationChangedPayload payload);

}
