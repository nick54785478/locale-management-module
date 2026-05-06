package com.example.demo.infra.adapter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.demo.application.port.CacheRefresherRegistryPort;
import com.example.demo.infra.locale.refresher.AbstractCacheRefresher;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring 快取刷新器註冊中心（Registry Adapter）
 *
 * <p>
 * 此類作為 {@link CacheRefresherRegistryPort} 在 Spring 環境下的具體實作， 負責：
 * </p>
 *
 * <ul>
 * <li>於應用程式啟動時收集所有 {@link AbstractCacheRefresher}</li>
 * <li>依照 {@link AbstractCacheRefresher#getType()} 建立查找對照表</li>
 * <li>提供統一入口，讓上層服務可依「類型」取得對應的 Refresher</li>
 * </ul>
 *
 * <p>
 * 在整體架構中扮演的角色：
 * </p>
 *
 * <ul>
 * <li>Infrastructure Layer 的 Adapter</li>
 * <li>負責連接 Domain 定義的 Registry Port 與 Spring Bean 生態</li>
 * <li>避免上層邏輯直接依賴 Spring 容器</li>
 * </ul>
 *
 * <p>
 * 設計特點：
 * </p>
 *
 * <ul>
 * <li>利用 Spring 的 {@code List<Bean>} 注入機制自動蒐集實作</li>
 * <li>於建構子階段完成註冊，屬於啟動期行為</li>
 * <li>查找成本為 O(1)，避免重複遍歷</li>
 * </ul>
 *
 * <p>
 * 注意事項：
 * </p>
 *
 * <ul>
 * <li>{@link AbstractCacheRefresher#getType()} 必須唯一</li>
 * <li>若出現重複 type，{@link Collectors#toMap} 將於啟動期拋出例外</li>
 * <li>此行為可視為一種 fail-fast 設計</li>
 * </ul>
 */
@Slf4j
@Component
public class SpringCacheRefresherRegistryAdapter implements CacheRefresherRegistryPort {

	/**
	 * Refresher 註冊表
	 *
	 * <p>
	 * Key ：Refresher 類型（{@link AbstractCacheRefresher#getType()}） Value ：對應的
	 * {@link AbstractCacheRefresher} 實例
	 * </p>
	 *
	 * <p>
	 * 此 Map 於建構子中初始化完成，之後僅進行查找，不會再被修改。
	 * </p>
	 */
	private final Map<String, AbstractCacheRefresher> refresherMap;

	/**
	 * 建構子：建立 Refresher Registry
	 *
	 * <p>
	 * Spring 會自動注入容器中所有 {@link AbstractCacheRefresher} 的實作， 並於此處：
	 * </p>
	 *
	 * <ol>
	 * <li>將其依 type 分組</li>
	 * <li>轉換為 Map 以利快速查找</li>
	 * <li>在啟動期即完成治理</li>
	 * </ol>
	 *
	 * <p>
	 * 若存在重複的 type，應用程式將於啟動期失敗， 以避免執行期出現不確定行為。
	 * </p>
	 *
	 * @param refreshers Spring 容器中所有 AbstractCacheRefresher 的實作清單
	 */
	public SpringCacheRefresherRegistryAdapter(List<AbstractCacheRefresher> refreshers) {
		this.refresherMap = refreshers.stream().collect(Collectors.toMap(AbstractCacheRefresher::getType, r -> r));

		// 啟動期輸出註冊結果，便於確認實際註冊的 Refresher
		log.info("Cache Refresher Registry initialized: {}", refresherMap);
	}

	/**
	 * 依 Refresher 類型取得對應的快取刷新器
	 *
	 * <p>
	 * 此方法為上層服務與應用層的主要使用入口， 可依據不同訊息類型動態取得對應的刷新策略。
	 * </p>
	 *
	 * <p>
	 * 若查無對應的 type，將回傳 {@code null}， 呼叫端應自行決定：
	 * </p>
	 *
	 * <ul>
	 * <li>忽略處理</li>
	 * <li>拋出業務例外</li>
	 * <li>回退至預設策略</li>
	 * </ul>
	 *
	 * @param type Refresher 類型識別（例如 SUCCESS_MESSAGE、EXCEPTION_MESSAGE）
	 * @return 對應的 {@link AbstractCacheRefresher}，若不存在則為 {@code null}
	 */
	@Override
	public AbstractCacheRefresher getRefresher(String type) {
		return refresherMap.get(type);
	}
}
