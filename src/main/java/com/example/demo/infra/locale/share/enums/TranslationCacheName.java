package com.example.demo.infra.locale.share.enums;

public enum TranslationCacheName {

	SUCCESS_MESSAGE("SuccessMessage", "SUCCESS_MESSAGE"), EXCEPTION_MESSAGE("ExceptionMessage", "EXCEPTION_MESSAGE");

	/**
	 * 快取名稱（對應 Spring Cache 名稱）
	 */
	private final String name;

	/**
	 * 類別型別（對應資料庫翻譯類別）
	 */
	private final String type;

	TranslationCacheName(String cacheName, String categoryType) {
		this.name = cacheName;
		this.type = categoryType;
	}

	/**
	 * 取得快取名稱
	 */
	public String getName() {
		return name;
	}

	/**
	 * 取得快取名稱（別名方法）
	 */
	public String cacheName() {
		return name;
	}

	/**
	 * 取得資料庫翻譯類別型別
	 */
	public String getType() {
		return type;
	}
}
