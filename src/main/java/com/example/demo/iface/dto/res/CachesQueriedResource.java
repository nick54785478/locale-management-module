package com.example.demo.iface.dto.res;

import com.example.demo.application.shared.CacheQueriedData;

public record CachesQueriedResource(String code, String message, CacheQueriedData data) {
}