package com.example.demo.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 日期轉換工具
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateTransformUtil {

	/**
	 * 將 LocalDate 轉為 字串
	 *
	 * @param pattern   日期模式
	 * @param localDate localDate字串
	 * @return 轉換後的字串
	 */
	public static String formatLocalDateToString(String pattern, LocalDate localDate) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		if (Objects.isNull(localDate)) {
			return null;
		}
		return localDate.format(formatter);
	}

	/**
	 * 將 字串 轉為 LocalDate
	 *
	 * @param pattern   日期模式
	 * @param localDate localDate字串
	 * @return LocalDate
	 */
	public static LocalDate parseStringToLocalDate(String pattern, String localDate) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		return LocalDate.parse(localDate, formatter);
	}

	/**
	 * String 轉換 Date
	 *
	 * @param pattern 日期模式
	 * @param date    日期字串
	 * @return Date 轉換後的日期
	 */
	public static Date parse(String pattern, String date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		if (StringUtils.isBlank(date)) {
			return null;
		}
		try {
			// 嘗試解析為 LocalDateTime
			LocalDateTime ldt = LocalDateTime.parse(date, formatter);
			return transformLocalDateTimeToDate(ldt);
		} catch (DateTimeParseException e1) {
			try {
				// 若失敗則嘗試 LocalDate
				LocalDate ld = LocalDate.parse(date, formatter);
				return transformLocalDateTimeToDate(ld.atStartOfDay());
			} catch (DateTimeParseException e2) {
				// 可選：直接回傳 null，或是改丟 IllegalArgumentException
				log.warn("日期轉換失敗", e2);
				return null;
			}
		}
	}

	/**
	 * Date 轉換 String
	 *
	 * @param pattern 日期模式
	 * @param date    日期
	 * @return String 轉換後的日期字串
	 */
	public static String format(String pattern, Date date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		if (Objects.isNull(date)) {
			return null;
		}
		LocalDateTime localDateTime = transformDateToLocalDateTime(date);
		return localDateTime == null ? null : localDateTime.format(formatter);
	}

	/**
	 * LocalDateTime 轉換為 String
	 *
	 * @param pattern       日期模式
	 * @param localDateTime LocalDateTime
	 */
	public static String formatLocalDateTimeToString(String pattern, LocalDateTime localDateTime) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		return localDateTime.format(formatter);
	}

	/**
	 * 將 LocalDateTime 轉換為 Date
	 *
	 * @param date LocalDateTime
	 * @return Date
	 */
	private static Date transformLocalDateTimeToDate(LocalDateTime date) {
		if (Objects.isNull(date)) {
			return null;
		}
		return Date.from(date.atZone(ZoneId.systemDefault()).toInstant());
	}

	/**
	 * 將 Date 轉換為 LocalDateTime
	 *
	 * @param date Date
	 * @return LocalDateTime
	 */
	private static LocalDateTime transformDateToLocalDateTime(Date date) {
		if (Objects.isNull(date)) {
			return null;
		}
		Instant instant = date.toInstant();
		return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	/**
	 * 將原始日期進行時間推移
	 *
	 * @param originalDate 原始日期
	 * @param amountToAdd  要推移的數量，正數為往後，負數為往前
	 * @param unit         推移單位，例如: ChronoUnit.HOURS、DAYS 等
	 * @return 調整後的日期
	 */
	public static Date shiftDate(Date originalDate, long amountToAdd, ChronoUnit unit) {
		if (originalDate == null) {
			return null;
		}
		Instant instant = originalDate.toInstant();
		Instant adjusted = instant.plus(amountToAdd, unit);
		return Date.from(adjusted);
	}

	/**
	 * 將字串轉換為 LocalDateTime
	 *
	 * @param pattern       日期模式
	 * @param localDateTime 字串
	 * @return LocalDateTime
	 */
	public static LocalDateTime parseStringToLocalDateTime(String pattern, String localDateTime) {
		if (localDateTime == null) {
			return null;
		}
		DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern(pattern).optionalStart()
				.appendPattern("HHmmss").optionalEnd().parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
				.parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0).parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
				.toFormatter();
		return LocalDateTime.parse(localDateTime, formatter);
	}

	public class DatePatternConstant {

		public static final String PATTERN1 = "yyyy-MM-dd";

		public static final String PATTERN2 = "yyyy-MM-dd HH:mm:ss";

		public static final String PATTERN3 = "yyyyMMdd";

	}

}
