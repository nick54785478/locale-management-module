package com.example.demo.iface.rest;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.application.service.LocaleConfigCommandService;
import com.example.demo.application.service.LocaleConfigQueryService;
import com.example.demo.application.shared.command.inbound.CreateLocaleConfigCommand;
import com.example.demo.application.shared.command.inbound.UpdateLocaleConfigCommand;
import com.example.demo.application.shared.dto.LocaleConfigGottenResult;
import com.example.demo.iface.dto.req.CreateLocaleConfigResource;
import com.example.demo.iface.dto.req.UpdateLocaleConfigResource;
import com.example.demo.iface.dto.res.LocaleConfigGottenResource;
import com.example.demo.iface.dto.res.LocaleConfigsGottenResource;
import com.example.demo.iface.dto.res.LocaleConfigCreatedResource;
import com.example.demo.iface.dto.res.LocaleConfigUpdatedResource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/locale-configs")
@AllArgsConstructor
@Tag(name = "Locale Config API", description = "語系管理 API (支援新增/修改/刪除/查詢可用語系)")
public class LocaleConfigController {

    private final LocaleConfigCommandService commandService;
    private final LocaleConfigQueryService queryService;

    @PostMapping
    @Operation(summary = "新增語系", description = "新增一筆語系設定。")
    public ResponseEntity<LocaleConfigCreatedResource> create(@RequestBody CreateLocaleConfigResource resource) {
        CreateLocaleConfigCommand command = new CreateLocaleConfigCommand(
                resource.getCode(),
                resource.getDisplayName(),
                resource.getEnabled(),
                resource.getRemark()
        );
        commandService.create(command);
        return new ResponseEntity<>(new LocaleConfigCreatedResource("200", "SUCCESS"), HttpStatus.OK);
    }

    @PutMapping
    @Operation(summary = "更新語系", description = "更新一筆已存在的語系設定。")
    public ResponseEntity<LocaleConfigUpdatedResource> update(@RequestBody UpdateLocaleConfigResource resource) {
        UpdateLocaleConfigCommand command = new UpdateLocaleConfigCommand(
                resource.getCode(),
                resource.getDisplayName(),
                resource.getEnabled(),
                resource.getRemark()
        );
        commandService.update(command);
        return new ResponseEntity<>(new LocaleConfigUpdatedResource("200", "SUCCESS"), HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "取得所有語系清單", description = "取得系統所有支援或已配置的語系。")
    public ResponseEntity<LocaleConfigsGottenResource> getAll() {
        List<LocaleConfigGottenResult> data = queryService.findAll();
        return new ResponseEntity<>(new LocaleConfigsGottenResource("200", "SUCCESS", data), HttpStatus.OK);
    }

    @GetMapping("/{code}")
    @Operation(summary = "查詢單一語系", description = "依據語系代碼取得詳細設定。")
    public ResponseEntity<LocaleConfigGottenResource> getByCode(@Parameter(description = "語系代碼", example = "zh_tw") @PathVariable String code) {
        Optional<LocaleConfigGottenResult> data = queryService.findByCode(code);
        return new ResponseEntity<>(new LocaleConfigGottenResource("200", "SUCCESS", data), HttpStatus.OK);
    }
}
