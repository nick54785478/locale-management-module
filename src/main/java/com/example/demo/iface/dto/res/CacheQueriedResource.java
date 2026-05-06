package com.example.demo.iface.dto.res;

import java.util.Optional;

public record CacheQueriedResource(String code, String message, Optional<Object> data) {
}
