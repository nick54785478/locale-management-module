package com.example.demo.iface.filter;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.demo.infra.shared.context.ContextHolder;

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

		// 從請求頭取得語系資料
		String lang = request.getHeader("lang");
		ContextHolder.setLang(StringUtils.isBlank(lang) ? "en_us" : lang);

		// 這邊先硬編碼，正是需從其他地方來，如: JWToken
		ContextHolder.setRoles(new ArrayList<>());
		ContextHolder.setUsername("nick123@example.com");

		// 放行
		filterChain.doFilter(request, response);
	}

}
