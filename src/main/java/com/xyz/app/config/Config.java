package com.xyz.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/**
 * 配置信息
 */
@Getter
@Setter
@Component
@ConfigurationProperties
public class Config {

	private String src;
	private String out;
	private Long size;

}
