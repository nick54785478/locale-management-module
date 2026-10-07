package com.example.demo.iface.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "多語系配置更新結果")
public record TranslateCategoryUpdatedResource(
    @Schema(description = "回應代碼", example = "200") String code, 
    @Schema(description = "回應訊息", example = "SUCCESS") String message) {
}
