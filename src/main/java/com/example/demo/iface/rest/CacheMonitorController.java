package com.example.demo.iface.rest;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.application.service.CacheQueryService;
import com.example.demo.application.shared.CacheQueriedData;
import com.example.demo.iface.dto.res.CacheQueriedResource;
import com.example.demo.iface.dto.res.CachesQueriedResource;

import lombok.AllArgsConstructor;

/**
 * 快取監控 Controller
 *
 * <p>
 * 提供 REST API 介面，用於查詢目前 JVM 內 Spring Cache（Caffeine）中的快取內容， 主要用途為：
 * </p>
 *
 * <ul>
 * <li>除錯（Debug）快取是否正確寫入</li>
 * <li>驗證 TTL / Cache 行為是否符合預期</li>
 * <li>輔助系統維運與問題排查</li>
 * </ul>
 *
 * <p>
 * ⚠️ 注意事項：
 * <ul>
 * <li>本 Controller <strong>僅建議在內部環境或非正式環境使用</strong></li>
 * <li>不建議對外公開，避免洩漏系統內部快取資料</li>
 * <li>不應承擔任何業務邏輯，僅作為查詢用途</li>
 * </ul>
 * </p>
 *
 * <p>
 * 實際快取存取行為由 {@link CacheQueryService} 與 {@code CacheMangerPort} 所負責，本
 * Controller 僅作為 API Facade。
 * </p>
 */
@RestController
@AllArgsConstructor
@RequestMapping("/cache")
public class CacheMonitorController {

	/**
	 * 快取查詢服務
	 */
	private CacheQueryService cacheQueryService;

	/**
	 * 查詢指定 cacheName 與 key 的快取內容
	 *
	 * <p>
	 * 若快取不存在、key 不存在，或快取實作不支援查詢， 皆會回傳 {@link Optional#empty()}。
	 * </p>
	 *
	 * <p>
	 * 回傳結果會保留 Optional 語意， 以清楚表達「快取未命中」與「快取值為 null」的差異。
	 * </p>
	 *
	 * @param cacheName 快取名稱（例如：ExceptionMessage）
	 * @param key       快取 key
	 * @return 指定 key 的快取值（Optional 包裝）
	 */
	@GetMapping("")
	public ResponseEntity<CacheQueriedResource> monitor(@RequestParam String cacheName, @RequestParam String key) {
		Optional<Object> op = cacheQueryService.getCache(cacheName, key);
		return new ResponseEntity<>(new CacheQueriedResource("200", "QUERIED_SUCCESS", op), HttpStatus.OK);
	}

	/**
	 * 取得指定快取內的所有 key / value
	 *
	 * <p>
	 * 注意：
	 * <ul>
	 * <li>僅支援 {@code CaffeineCache} 實作</li>
	 * <li>若快取不存在或非 Caffeine，將回傳空 Map</li>
	 * <li>在快取資料量大時請謹慎使用，避免效能影響</li>
	 * </ul>
	 * </p>
	 *
	 * @param cacheName 快取名稱
	 * @return 快取內所有資料（key / value）
	 */
	@GetMapping("/all")
	public ResponseEntity<CachesQueriedResource> getAllCache(@RequestParam String cacheName) {
		CacheQueriedData responseData = cacheQueryService.getAllCache(cacheName);
		return new ResponseEntity<>(new CachesQueriedResource("200", "QUERY_SUCCESS", responseData), HttpStatus.OK);
	}
}
