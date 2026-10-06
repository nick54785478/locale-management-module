package com.example.demo.iface.filter;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.demo.application.shared.context.ContextHolder;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 上下文攔截器，在此處設置各類上下文資訊，如: 使用者資訊、JWT Token 等
 */
@Slf4j
@Component
public class ContextHolderFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		// 1. 透過 Servlet 原生解析標準的 Accept-Language Header
		// request.getLocale().toLanguageTag() 會回傳類似 "zh-TW", "en-US" 等標準格式
		String lang = request.getLocale().toLanguageTag(); 

		// 2. 正規化：統一轉小寫，並把減號替換為底線 (配合資料庫的 zh_tw, en_us, zh_cn 格式)
		String normalizedLang = "en_us"; // 預設值
		if (StringUtils.isNotBlank(lang) && !"und".equals(lang)) {
			normalizedLang = lang.toLowerCase().replace("-", "_");
		}
		
		ContextHolder.setLang(normalizedLang);

		// 這邊先硬編碼，正是需從其他地方來，如: JWToken
		ContextHolder.setRoles(new ArrayList<>());
		ContextHolder.setUsername("nick123@example.com");

		// 放行
		filterChain.doFilter(request, response);
	}

}
