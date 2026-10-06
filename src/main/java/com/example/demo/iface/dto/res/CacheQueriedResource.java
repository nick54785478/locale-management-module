package com.example.demo.iface.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Optional;

@Schema(description = "單一快取查詢結果")
public record CacheQueriedResource(
    @Schema(description = "回應代碼", example = "200") String code, 
    @Schema(description = "回應訊息", example = "QUERIED_SUCCESS") String message, 
    @Schema(description = "快取資料") Optional<Object> data) {
}
