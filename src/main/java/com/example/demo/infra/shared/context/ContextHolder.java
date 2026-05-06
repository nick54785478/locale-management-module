package com.example.demo.infra.shared.context;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * 上下文工具類
 */
@Slf4j
public class ContextHolder {

	/**
	 * 儲存 JWT Token
	 */
	private static final ThreadLocal<String> JWT_TOKEN = new ThreadLocal<>();

	/**
	 * 角色清單
	 */
	private static final ThreadLocal<List<String>> ROLES = new ThreadLocal<>();

	/**
	 * 使用者名稱
	 */
	private static final ThreadLocal<String> USERNAME = new ThreadLocal<>();

	/**
	 * 語系
	 */
	private static final ThreadLocal<String> LANG = new ThreadLocal<>();

	/**
	 * 取得 JWT Token
	 */
	public static String getJwtToken() {
		return JWT_TOKEN.get();
	}

	/**
	 * 設置 JWT Token
	 *
	 * @param token JWT Token
	 */
	public static void setJwtToken(String token) {
		JWT_TOKEN.set(token);
	}

	/**
	 * 設置 角色清單
	 *
	 * @param roles 角色清單
	 */
	public static void setRoles(List<String> roles) {
		ROLES.set(roles);
	}

	/**
	 * 設置當前登入者的語系資料
	 *
	 * @param lang 語系
	 */
	public static void setLang(String lang) {
		LANG.set(lang);
	}

	/**
	 * 設置當前登入者名稱
	 *
	 * @param username 語系
	 */
	public static void setUsername(String username) {
		USERNAME.set(username);
	}

	/**
	 * 取得目前登入者的角色清單
	 *
	 * @return 目前登入者角色清單
	 */
	public static List<String> getRoleList() {
		return ROLES.get() != null ? ROLES.get() : new ArrayList<>();
	}

	/**
	 * 取得當前語系資料
	 *
	 * @return 目前登入者語系資料
	 */
	public static String getLang() {
		return LANG.get() != null ? LANG.get() : null;
	}

	/**
	 * 取得當前使用者名稱
	 *
	 * @return 目前登入者名稱
	 */
	public static String getUsername() {
		return USERNAME.get() != null ? USERNAME.get() : null;
	}

	/**
	 * 清理線程資料
	 */
	public static void clear() {
		JWT_TOKEN.remove();
		ROLES.remove();
		LANG.remove();
	}

}
