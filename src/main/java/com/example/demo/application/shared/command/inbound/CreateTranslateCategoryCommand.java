package com.example.demo.application.shared.command.inbound;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTranslateCategoryCommand {

  private String type;
  private String code;
  private String description;

  private List<CreateTranslateCommand> translations = new ArrayList<>();

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CreateTranslateCommand {
    private String language;
    private String textValue;
    private String remark;
  }
}
