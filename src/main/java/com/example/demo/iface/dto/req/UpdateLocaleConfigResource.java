package com.example.demo.iface.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "更新語系請求物件")
public class UpdateLocaleConfigResource {

    @Schema(description = "語系代碼", example = "zh_tw")
    private String code;

    @Schema(description = "顯示名稱", example = "繁體中文")
    private String displayName;

    @Schema(description = "是否啟用", example = "true")
    private Boolean enabled;

    @Schema(description = "備註", example = "預設語系")
    private String remark;
}
