package com.example.demo.application.shared;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 封裝快取查詢的實際資料內容
 *
 * <p>
 * 包含快取名稱以及該快取中的所有快取紀錄。
 * </p>
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CacheQueriedData {

	/**
	 * Cache 名稱
	 */
	private String cacheName;

	/**
	 * 快取中每個紀錄的詳細資料
	 */
	private List<CacheMetaData> details = new ArrayList<>();

	/**
	 * 單筆快取紀錄資訊
	 *
	 * <p>
	 * 描述快取的 key 與對應的 value。
	 * </p>
	 */
	@Data
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CacheMetaData {

		/**
		 * 快取 key
		 */
		private String key;

		/**
		 * 對應的快取值，可為任意物件
		 */
		private Object value;
	}
}