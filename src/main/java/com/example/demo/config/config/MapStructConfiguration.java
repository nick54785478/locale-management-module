package com.example.demo.config.config;

import org.mapstruct.MapperConfig;

import com.example.demo.infra.mapper.BaseDataTransformMapper;

/**
 * MapStruct 的配置類
 */
@MapperConfig(componentModel = "spring", uses = { BaseDataTransformMapper.class })
public interface MapStructConfiguration {
}
