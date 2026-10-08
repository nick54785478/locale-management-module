package com.example.demo.application.shared.command.outbound;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateLocaleConfigPortCommand {
    private String code;
    private String displayName;
    private Boolean enabled;
    private String remark;
}
