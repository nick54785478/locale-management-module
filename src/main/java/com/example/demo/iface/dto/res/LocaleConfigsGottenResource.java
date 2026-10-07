package com.example.demo.iface.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import com.example.demo.application.shared.dto.LocaleConfigGottenResult;
import java.util.List;

@Schema(description = "多筆語系配置查詢結果")
public record LocaleConfigsGottenResource(
    @Schema(description = "回應代碼", example = "200") String code, 
    @Schema(description = "回應訊息", example = "SUCCESS") String message, 
    @Schema(description = "語系配置清單") List<LocaleConfigGottenResult> data) {
}
