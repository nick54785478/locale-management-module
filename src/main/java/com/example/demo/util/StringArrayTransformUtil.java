package com.example.demo.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringArrayTransformUtil {

	/**
	 * 以逗號分隔的字串轉換為 List<String>
	 *
	 * @param input 來源字串 (例如 "A,B,C")
	 * @return List<String>
	 */
	public static List<String> toList(String input) {
		if (input == null || input.trim().isEmpty()) {
			return Collections.emptyList();
		}
		return Arrays.stream(input.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList();
	}

	/**
	 * List<String> 轉換為以逗號分隔的字串
	 *
	 * @param list List<String>
	 * @return String (例如 "A,B,C")
	 */
	public static String toString(List<String> list) {
		if (list == null || list.isEmpty()) {
			return "";
		}
		return String.join(",", list);
	}

	/**
	 * 以逗號分隔的字串轉換為 Set<String> (保留輸入順序並自動去重)
	 *
	 * @param input 來源字串 (例如 "A,B,C,A")
	 * @return Set<String>
	 */
	public static Set<String> toSet(String input) {
		if (input == null || input.trim().isEmpty()) {
			return Collections.emptySet();
		}
		return Arrays.stream(input.split(",")).map(String::trim).filter(s -> !s.isEmpty())
				.collect(Collectors.toCollection(LinkedHashSet::new));
	}

	/**
	 * Set<String> 轉換為以逗號分隔的字串
	 *
	 * @param set Set<String>
	 * @return String (例如 "A,B,C")
	 */
	public static String toString(Set<String> set) {
		if (set == null || set.isEmpty()) {
			return "";
		}
		return String.join(",", set);
	}

}
