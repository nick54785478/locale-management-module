package com.example.demo.iface.dto.req;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveTranslateCategoryResource {

  private String type;  // 分類類型

  private String code; // 分類代碼

  private String description; // 分類描述

  private List<SaveTranslateResource> translations = new ArrayList<>();

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class SaveTranslateResource {

    private String language; // 語言代碼

    private String textValue;  // 文字內容

    private String remark; // 備註
  }
}
