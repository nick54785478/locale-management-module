package com.example.demo.infra.locale.share.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 多語系內容異動 Payload
 *
 * <p>
 * 此類為資料傳輸物件（DTO）， 用於承載「單一語系翻譯內容」的最新狀態， 常見用途包括：
 * </p>
 *
 * <ul>
 * <li>寫入快取（Cache Payload）</li>
 * <li>作為事件或同步流程中的資料載體</li>
 * </ul>
 *
 * <p>
 * 設計定位：
 * </p>
 *
 * <ul>
 * <li>不是 Domain Entity</li>
 * <li>不包含任何業務邏輯</li>
 * <li>欄位設計偏向「讀取與顯示用途」</li>
 * </ul>
 *
 * <p>
 * 在本系統中，通常由 {@code TranslationMapper} 轉換產生， 並被 {@code AbstractCacheRefresher}
 * 寫入快取。
 * </p>
 *
 * <p>
 * 快取資料示意：
 * </p>
 *
 * <pre>
 * key   : USER_LOGIN_SUCCESS:zh_tw
 * value : TranslationChangedPayload
 * </pre>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TranslationChangedPayload {

	/**
	 * 翻譯資料的唯一識別碼
	 *
	 * <p>
	 * 對應於 Translation Entity 的 UUID， 主要用於：
	 * </p>
	 *
	 * <ul>
	 * <li>資料追蹤</li>
	 * <li>除錯與記錄</li>
	 * </ul>
	 */
	private String uuid;

	/**
	 * 備註說明
	 *
	 * <p>
	 * 可用於補充翻譯來源、使用情境或管理用途的說明文字， 非顯示給終端使用者的必要欄位。
	 * </p>
	 */
	private String remark;

	/**
	 * 語系代碼
	 *
	 * <p>
	 * 例如：
	 * </p>
	 *
	 * <ul>
	 * <li>zh_TW</li>
	 * <li>en_US</li>
	 * </ul>
	 *
	 * <p>
	 * 在快取 key 中通常會轉為小寫處理， 以確保語系查找一致性。
	 * </p>
	 */
	private String language;

	/**
	 * 實際顯示的翻譯文字內容
	 *
	 * <p>
	 * 此欄位為前端或呼叫端最終使用的文字， 例如成功訊息、錯誤提示或說明文字。
	 * </p>
	 */
	private String textValue;
}
