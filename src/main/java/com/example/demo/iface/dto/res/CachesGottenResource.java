package com.example.demo.iface.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import com.example.demo.application.shared.dto.CacheGottenResult;

@Schema(description = "全部快取查詢結果")
public record CachesGottenResource(
    @Schema(description = "回應代碼", example = "200") String code, 
    @Schema(description = "回應訊息", example = "QUERY_SUCCESS") String message, 
    @Schema(description = "快取資料") CacheGottenResult data) {
}