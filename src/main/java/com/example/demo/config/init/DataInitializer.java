package com.example.demo.config.init;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.example.demo.application.shared.command.SaveTranslateCategoryCommand;
import com.example.demo.application.service.TranslationCommandService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DataInitializer {

  @Autowired
  private TranslationCommandService translationCommandService;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @PostConstruct
  public void init() {
    // 讀取後端某個 Json 檔案，並轉為 Commands
    try {
      // 1. 讀取 classpath 下的 JSON 檔案
      Resource resource = new ClassPathResource("init-data.json");

      if (! resource.exists()) {
        log.warn("JSON file not found: {}", resource.getFilename());
        return;
      }

      // 2. 轉成 List<SaveTranslateCategoryCommand>
      List<SaveTranslateCategoryCommand> commands = objectMapper.readValue(
        resource.getInputStream(),
        new TypeReference<List<SaveTranslateCategoryCommand>>() {
        }
      );

      // 3. 正規化 JSON 中的語系代碼，確保不論 JSON 寫的是 zh-tw 還是 zh-TW，進到系統都轉為 zh_tw
      commands.forEach(category -> {
          if (category.getTranslations() != null) {
              category.getTranslations().forEach(translation -> {
                  if (translation.getLanguage() != null) {
                      String normalized = translation.getLanguage().toLowerCase().replace("-", "_");
                      translation.setLanguage(normalized);
                  }
              });
          }
      });

      // 4. 這裡可以進一步處理，例如呼叫 Application Service 儲存
      translationCommandService.saveTranslateCategoryList(commands);

    } catch (IOException e) {
      log.error("Failed to read JSON file", e);
    }

  }

}
