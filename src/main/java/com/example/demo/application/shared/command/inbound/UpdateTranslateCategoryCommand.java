package com.example.demo.application.shared.command.inbound;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTranslateCategoryCommand {

  private String type;
  private String code;
  private String description;

  private List<UpdateTranslateCommand> translations = new ArrayList<>();

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class UpdateTranslateCommand {
    private String language;
    private String textValue;
    private String remark;

    public String getLanguage() {
        return language == null ? null : language.toLowerCase().replace("-", "_");
    }
  }
}
