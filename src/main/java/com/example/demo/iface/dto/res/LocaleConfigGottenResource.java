package com.example.demo.iface.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import com.example.demo.application.shared.dto.LocaleConfigGottenResult;
import java.util.Optional;

@Schema(description = "單筆語系配置查詢結果")
public record LocaleConfigGottenResource(
    @Schema(description = "回應代碼", example = "200") String code, 
    @Schema(description = "回應訊息", example = "SUCCESS") String message, 
    @Schema(description = "語系配置") Optional<LocaleConfigGottenResult> data) {
}
