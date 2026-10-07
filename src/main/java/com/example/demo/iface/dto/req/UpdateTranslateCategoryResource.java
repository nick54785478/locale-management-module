package com.example.demo.iface.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "更新多語系配置請求物件")
public class UpdateTranslateCategoryResource {

  @Schema(description = "分類類型 (例: ErrorMessage, UI_Text)", example = "ErrorMessage")
  private String type;

  @Schema(description = "分類代碼 (例: E001)", example = "E001")
  private String code;

  @Schema(description = "分類描述", example = "系統錯誤代碼")
  private String description;

  @Schema(description = "多語系翻譯內容清單")
  private List<UpdateTranslateResource> translations = new ArrayList<>();

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Schema(description = "多語系翻譯內容項目")
  public static class UpdateTranslateResource {

    @Schema(description = "語言代碼 (例: zh-TW, en-US)", example = "zh-TW")
    private String language;

    @Schema(description = "文字內容", example = "發生預期外錯誤")
    private String textValue;

    @Schema(description = "備註", example = "前端顯示用")
    private String remark;
  }
}
