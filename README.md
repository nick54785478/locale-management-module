# Multi-Language Translation Module

本模組提供系統級的國際化（i18n）解決方案，旨在統一管理 業務例外訊息（Exception Messages） 與 API 成功回應訊息（Success Messages）。透過高效的快取機制與 AOP 攔截，實現對開發者透明、對效能友好的多語系轉譯。


## 本模組旨在解決以下問題：

* 管理多語系資料(可動態擴充語系)
* 統一 多語系例外訊息（Exception Message） 的管理方式
* 避免例外發生時頻繁存取資料庫 --> 提供可觀測、可除錯的 JVM In-Memory Cache
* 符合 Clean Architecture / Hexagonal Architecture
* 快取策略（TTL、容量）與業務邏輯完全解耦


## 核心特性

* Template Method 模式：統一轉譯流程，子類僅需關注特定的業務類別與快取配置。
* AOP 無感轉譯：自動攔截 Response Body，支援 Java Record 與 POJO 的訊息替換。
* 高性能快取：整合 Caffeine (JVM In-Memory)，支援負向快取（Negative Caching）防止快取穿透。
* 動態維護 (DDD)：符合 DDD 規範的 Aggregate 設計，支援在不重啟服務的情況下動態調整翻譯內容。
* 語系自動解析：透過 Filter 自動從 Request Header 提取語系並維護上下文。

## 系統架構與流程

**1. 語系解析與上下文 (Language Resolution)**
所有請求進入系統時，首站會經過 ContextHolderFilter：
>* 來源：讀取 Header 中的 lang 欄位（例如：zh_tw, en_us）。
>* 預設值：若未帶 Header，預設回退至 en_us。
>* 儲存：存入基於 ThreadLocal 的 ContextHolder，供後續轉譯器讀取。

**2. 轉譯器設計 (Message Translators)**

採用模板方法模式，核心邏輯封裝於 AbstractMessageTranslator：

| 元件名稱 | 負責語義 | 快取名稱 (Cache Name) |
| --- | --- | --- |
| LocaleExceptionMessageTranslator | 業務例外（Errors）| ExceptionMessage |
| LocaleSuccessMessageTranslator | 成功回應（Success） | SuccessMessage |

* 正規化：自動處理語系字串（如 zh-TW 轉為 zh_tw），確保快取 Key 一致性。

* 快取 Key 格式：{messageKey}:{lang}。


## 領域模型設計 (Domain Model)

遵循 DDD 聚合原則，確保資料的一致性與封裝：
>* TranslationCategory (Aggregate Root)：表示一組「相同語意」的翻譯集合（例如一個錯誤代碼）。
>* Translation (Entity)：具體各個語系的文字內容。
>* TranslationCategory 與 Translation 為一對多關係。

設計亮點：所有翻譯的異動（新增/修改）皆須透過 TranslationCategory 進行，Service 層僅負責呼叫聚合根的方法，不直接操作內部集合。


## 攔截與轉譯邏輯

**全域例外攔截 (GlobalExceptionHandler)**
>* 攔截繼承自 BaseLocalizableException 的異常。
>* Fallback 機制：若指定的 messageKey 查無翻譯，系統自動嘗試轉譯 DEFAULT_ERROR 碼，確保回應不為空。

**全域成功回應處理 (GlobalLocaleResponseHandler)**

利用 ResponseBodyAdvice 在資料寫入 HTTP Response 前進行最後加工：

>* POJO 處理：透過 Java Bean 規範的 Getter/Setter 存取 message 欄位，符合安全規範。

>* Record 處理：由於 Record 是 Immutable 的，系統會透過反射（Reflection）讀取組件並調用全參數建構子（Canonical Constructor）重新建立物件。


## 快取策略 (Caffeine Configuration)

本模組為了兼顧「開發便利性」與「底層操作彈性」，採用了雙層快取管理設計。

**1. 雙層快取策略**
>* 宣告式快取 (Spring Cache Annotation)：
在 Translator 層級直接使用 @Cacheable 與 @CachePut。這讓業務邏輯保持簡潔，並能自動處理高頻率的翻譯查詢。
>* 程式化管理 (CacheManager Adapter)：
透過 CacheManagerPort 接口，允許在特殊情境下（如後台強制刷新、快取統計觀測）直接手動操作快取內容。

**2. 快取端口與配適器 (Port & Adapter)**

為了將業務邏輯與特定的快取實作（如 Caffeine 或 Redis）解耦，我們定義了標準接口：

**CacheManagerPort (接口)** : 定義了快取操作的標準行為：
>* put(cacheName, key, value)：手動存入快取。
>* evict(cacheName, key)：移除指定 Key 的快取資料。
>* clear(cacheName)：清空特定快取分區。
>* get(cacheName, key)：取得快取值，並回傳 Optional。
>* getAll(cacheName)：獲取指定快取內所有的 Key-Value 對（用於監控或除錯）。

**SpringCacheAdapter (實作)** : 基於 Spring Cache 抽象進行實作，目前針對 Caffeine Cache 優化：
>* Native Access：直接操作底層 Native Cache，效能損耗極低。
>* Optional Unwrap：由於 Spring Cache 存儲時可能會將 Optional 序列化，Adapter 會自動處理 Unwrap 邏輯，確保上層呼叫時拿到的是正確的型別。
>* 一致性保證：確保 @Cacheable 寫入的資料與手動透過 Port 寫入的資料格式一致，避免讀取異常。

**3. 快取防禦設計 (Cache Invariant)**
>* 負向快取 (Negative Caching)：當資料庫中不存在某個翻譯時，轉譯器回傳 Optional.empty()，Adapter 仍會將此結果快取。這能有效防止 快取穿透（Cache Penetration），避免惡意請求或無效 Key 反覆衝擊資料庫。
>* 自動正規化：所有的 Key 在進入快取層前皆會經過 normalizeLang() 處理，避免 zh-TW 與 zh_tw 被視為不同 Key 而造成記憶體浪費。


## 多語系例外設計 (Localizable Exceptions Design)


本模組的核心哲學是「邏輯與表現分離」。業務層（Service/Domain）在拋出錯誤時，不應關心最終的文字描述，只需提供定位錯誤所需的關鍵資訊。

**BaseLocalizableException**

所有「可多語化」業務例外的基底類別。它繼承自 RuntimeException，並強制作為一個攜帶上下文的容器。

**核心職責：**
>* 錯誤碼 (code)：系統內部的唯一錯誤識別碼（如：E0001），用於對應特定的處理邏輯或前端顯示。
>* 多語 Key (messageKey)：對應資料庫 TranslationCategory 的代碼，決定要抓取哪一組翻譯。
>* 動態參數 (fields)：一個 Map<String, String>，用於存放訊息中的變數（如：{ "username": "Nick" }），供轉譯器後續進行字串格式化。

**為什麼這樣設計？**
>* 保持 Clean Architecture：Domain 層不需要依賴 Translator 也不需要知道使用者的語系，它只管拋出正確的 Key。
>* 型別安全與擴充性：開發者可以針對不同業務情境繼承此類別（如：UserNotFoundException, InsufficientBalanceException），且全域攔截器（GlobalExceptionHandler）能統一識別並處理。
>* 減少重複程式碼：透過將轉譯邏輯集中在 Exception Handler，我們避免了在每個 catch 區塊中重複撰寫 translator.get(...) 的混亂。

**使用範例**

	public class UserNotFoundException extends BaseLocalizableException {
	    public UserNotFoundException(String userId) {
	        // 提供錯誤碼、多語 Key，以及動態參數
	        super("E_USER_001", "EXCEPTION.USER_NOT_FOUND", Map.of("id", userId));
	    }
	}
	


## 開發者指南

**如何新增一筆翻譯？**

1. 在資料庫中插入一筆 TranslationCategory，並指定 type（如 SUCCESS_MESSAGE）。

2. 在 Translation 表中插入該 Category 下不同 language 的文字內容。

3. 調用 refreshCache API 刷新 JVM 記憶體。

**在程式碼中使用**

拋出多語系例外：

	// 系統會自動根據當前語系查找 "USER_NOT_FOUND" 內容
	throw new UserNotFoundException("USER_NOT_FOUND");

回傳多語系成功訊息：

	// 在 Controller 回傳的 DTO 中，message 設為 Key 值
	return ApiResponse.success("SUCCESS_CREATED", userData);

	

