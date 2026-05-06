package com.example.demo.iface.dto.res;

import com.example.demo.application.shared.TranslateCategoryQueriedData;

public record TranslateCategoryQueriedResource(String code, String message, TranslateCategoryQueriedData data) {
}
