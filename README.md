# Multi-Language Translation Module

本模組提供系統級的國際化（i18n）解決方案，旨在統一管理 業務例外訊息（Exception Messages） 與 API 成功回應訊息（Success Messages）。透過高效的快取機制與 AOP 攔截，實現對開發者透明、對效能友好的多語系轉譯。


## 本模組旨在解決以下問題：

* 管理多語系資料(可動態擴充語系)
* 提供系統級的語系配置管理 (動態啟用/停用支援語系)
* 統一 多語系例外訊息（Exception Message） 的管理方式
* 避免例外發生時頻繁存取資料庫 --> 提供可觀測、可除錯的 JVM In-Memory Cache
* 嚴格符合 Clean Architecture / Hexagonal Architecture (Port & Adapter)
* 快取策略（TTL、容量）與業務邏輯完全解耦


## 核心特性

* Template Method 模式：統一轉譯流程，子類僅需關注特定的業務類別與快取配置。
* AOP 無感轉譯：自動攔截 Response Body，支援 Java Record 與 POJO 的訊息替換。
* 高性能快取：整合 Caffeine (JVM In-Memory)，支援負向快取（Negative Caching）防止快取穿透。
* 動態維護 (DDD)：符合 DDD 規範的 Aggregate 設計，支援在不重啟服務的情況下動態調整翻譯內容。
* 語系自動解析：透過 Filter 自動從標準的 `Accept-Language` Header 提取語系並維護上下文。

## 系統架構與流程

**1. 語系解析與上下文 (Language Resolution)**
所有請求進入系統時，首站會經過 ContextHolderFilter：
>* 來源：解析標準 HTTP `Accept-Language` Header（例如：`zh-TW`, `en-US`）。
>* 正規化與預設值：為了對接國際標準與確保系統內部比對防呆，系統會自動將語言標籤轉為小寫加底線（例如 `zh_tw`）。若未帶 Header 則預設為 `en_us`。
>* 儲存：存入基於 ThreadLocal 的 ContextHolder，供後續轉譯器讀取。

**2. 轉譯器設計 (Message Translators)**

採用模板方法模式，核心邏輯封裝於 AbstractMessageTranslator：

| 元件名稱 | 負責語義 | 快取名稱 (Cache Name) |
| --- | --- | --- |
| LocaleExceptionMessageTranslator | 業務例外（Errors）| ExceptionMessage |
| LocaleSuccessMessageTranslator | 成功回應（Success） | SuccessMessage |

* 正規化：自動處理語系字串（如 zh-TW 轉為 zh_tw），確保快取 Key 一致性。

* 快取 Key 格式：{messageKey}:{lang}。

**流程圖示意**

	graph TD
	    %% 1. 語系解析階段
	    Start((API Request)) --> Filter[ContextHolderFilter]
	    
	    subgraph "Phase 1: Language Resolution (攔截器層)"
	        Filter --> HeaderCheck{Header 包含 lang?}
	        HeaderCheck -- Yes --> SetLang[ContextHolder.setLang]
	        HeaderCheck -- No  --> SetDefault[預設為 en_us]
	        SetDefault --> SetLang
	    end
	
	    %% 2. 業務執行階段
	    SetLang --> Business[執行業務邏輯 / Service]
	
	    subgraph "Phase 2: Execution & Response (處理層)"
	        Business -- "拋出 Exception" --> ExHandler[GlobalExceptionHandler]
	        Business -- "正常回傳 Result" --> ResHandler[GlobalLocaleResponseHandler]
	    end
	
	    %% 3. 轉譯核心階段
	    subgraph "Phase 3: Translation Core (轉譯核心層)"
	        ExHandler  --> ExTrans[LocaleExceptionMessageTranslator]
	        ResHandler --> SucTrans[LocaleSuccessMessageTranslator]
	        
	        ExTrans  --> Template[AbstractMessageTranslator<br/>Template Method]
	        SucTrans --> Template
	        
	        Template --> Normalizer[語系正規化: lang.toLowerCase]
	        Normalizer --> CacheCheck{CacheManager 命中?}
	        
	        CacheCheck -- Hit  --> Return[返回翻譯結果]
	        CacheCheck -- Miss --> DB[(Translation Repository)]
	        DB --> Return
	    end
	
	    %% 4. 回傳階段
	    Return --> FinalResponse((API Response))


## 領域模型與架構設計 (Domain Model & Hexagonal Architecture)

遵循 DDD 聚合原則與六角架構，確保資料的一致性與完全解耦：

**1. 領域實體與聚合 (Domain Entities)**
>* TranslationCategory (Aggregate Root)：表示一組「相同語意」的翻譯集合（例如一個錯誤代碼）。
>* Translation (Entity)：具體各個語系的文字內容。
>* LocaleConfig (Entity)：管理系統支援的語系代碼與狀態。

**2. 嚴格的六角架構 (Port & Adapter)**
>* CQS (Command Query Separation)：應用層 (Application) 區分寫入 (Command Service) 與查詢 (Query Service)。
>* Inbound & Outbound Commands：Controller 向 Service 傳遞 `Inbound Command`；Service 向 Port (Infra) 傳遞 `Outbound PortCommand`。
>* 防腐層 (Anti-Corruption Layer)：JPA Entities (如 `TranslationCategory`, `LocaleConfig`) 絕對不會跨越至 Application 層。Infra 層的 Adapter 負責將 `PortCommand` 轉為 Entity 寫入，並將查詢的 Entity 轉為 DTO (`GottenResult`) 回傳，完美隔絕底層框架污染。

## 動態語系配置 (Locale Config)

本模組內建了完整的動態語系管理機制，不需修改程式碼即可控制系統支援的語言：
>* **動態驗證**：在新增或更新翻譯時，系統會自動驗證目標語系是否已註冊，若無則拋出 `LocaleNotFoundException` 並阻斷寫入。
>* **資料初始化**：系統啟動時會自動讀取 `init-data.json`，透過 Application Service 批次初始化基礎語系（如 zh-TW, en-US）與預設錯誤/成功訊息。



## 攔截與轉譯邏輯

**全域例外攔截 (GlobalExceptionHandler)**
>* 攔截繼承自 BaseLocalizableException 的異常。
>* Fallback 機制：若指定的 messageKey 查無翻譯，系統自動嘗試轉譯 DEFAULT_ERROR 碼，確保回應不為空。

**全域成功回應處理 (GlobalLocaleResponseHandler)**

利用 ResponseBodyAdvice 在資料寫入 HTTP Response 前進行最後加工：
>* POJO 處理：透過 Java Bean 規範的 Getter/Setter 存取 message 欄位，符合安全規範。
>* Record 處理：由於 Record 是 Immutable 的，系統會透過反射（Reflection）讀取組件並調用全參數建構子（Canonical Constructor）重新建立物件。


## 快取策略與動態切換 (Caching Strategy & Dynamic Toggle)

本模組為了兼顧「開發便利性」、「底層操作彈性」以及「分散式架構擴展性」，採用了雙層快取管理與條件式註冊設計。

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

**雙適配器實作 (Dual Adapters)**：
>* **SpringCacheAdapter (預設 - Caffeine)**：基於 Spring Cache 抽象進行實作，針對 Caffeine Cache 進行 Native Access 最佳化，提供極低延遲的單機快取。並自動處理 Optional Unwrap 邏輯。
>* **RedisCacheAdapter (備用 - Redis)**：基於 `RedisTemplate` 實作，適合跨服務/微服務架構共享快取。內建 Key 隔離機制 (`cacheName::key`) 與批次清理。

**3. 一鍵切換快取機制 (Dynamic Toggle)**
系統內建了條件式註冊 (@ConditionalOnProperty)，開發者只需修改 `application.properties` 即可無縫切換全域快取引擎（包含 @Cacheable 與 CacheManagerPort）：
```properties
# 可選值: caffeine (單機預設), redis (分散式共享)
app.cache.type=caffeine
```
切換後系統將自動抽換底層的 CacheManager 與相對應的 Adapter，Application 層業務邏輯完全「零感知、免修改」！

**4. 快取防禦設計 (Cache Invariant)**
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

1. 準備 Payload 並透過 Controller 呼叫對應的 Inbound Command (例如 `CreateTranslateCategoryCommand`)。
2. Service 層會自動校驗 Payload 內的語系是否存在於 `LocaleConfig` 中。
3. 寫入 Infra 層後，系統會自動觸發 `CacheRefresherRegistryPort` 完成 JVM 快取的刷新，無須人工介入。

**在程式碼中使用**

拋出多語系例外：

	// 系統會自動根據當前語系查找 "USER_NOT_FOUND" 內容
	throw new UserNotFoundException("USER_NOT_FOUND");

回傳多語系成功訊息：

	// 在 Controller 回傳的 DTO 中，message 設為 Key 值
	return ApiResponse.success("SUCCESS_CREATED", userData);

	

