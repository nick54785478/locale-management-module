package com.example.demo.application.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocaleConfigGottenResult {
    private String code;
    private String displayName;
    private Boolean enabled;
    private String remark;
}
