package com.example.demo.iface.dto.res;

import com.example.demo.application.shared.dto.TranslateCategoryGottenResult;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "多語系配置查詢結果")
public record TranslateCategoryGottenResource(
    @Schema(description = "回應代碼", example = "200") String code, 
    @Schema(description = "回應訊息", example = "SUCCESS") String message, 
    @Schema(description = "多語系配置資料") TranslateCategoryGottenResult data) {
}
