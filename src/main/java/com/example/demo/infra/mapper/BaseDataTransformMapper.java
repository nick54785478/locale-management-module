package com.example.demo.infra.mapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Named;

import com.example.demo.util.DateTransformUtil;
import com.example.demo.util.JsonParseUtil;
import com.example.demo.util.StringArrayTransformUtil;

/**
 * Base Data Transforming Mapper
 */
@Mapper(componentModel = "spring")
public interface BaseDataTransformMapper {

	/**
	 * 字串 Parse 成 Date
	 *
	 * @param dateStr 日期字串
	 * @return date
	 */
	@Named("parseStringToDate")
	default Date parseStringToDate(String dateStr) {
		return DateTransformUtil.parse(DateTransformUtil.DatePatternConstant.PATTERN2, dateStr);
	}

	/**
	 * 字串 Parse 成 LocalDate
	 *
	 * @param dateStr 日期字串
	 * @return date
	 */
	@Named("parseStringToLocalDate")
	default LocalDate parseStringToLocalDate(String dateStr) {
		return DateTransformUtil.parseStringToLocalDate(DateTransformUtil.DatePatternConstant.PATTERN1, dateStr);
	}

	/**
	 * Date Format 成 字串
	 *
	 * @param date Date
	 * @return String
	 */
	@Named("formatDateToString")
	default String formatDateToString(Date date) {
		return DateTransformUtil.format(DateTransformUtil.DatePatternConstant.PATTERN2, date);
	}

	/**
	 * LocalDate 轉字串
	 *
	 * @param date LocalDate
	 * @return String
	 */
	@Named("formatLocalDateToString")
	default String formatLocalDateToString(LocalDate date) {
		return DateTransformUtil.formatLocalDateToString(DateTransformUtil.DatePatternConstant.PATTERN1, date);
	}

	/**
	 * LocalDateTime 轉字串
	 *
	 * @param dateTime LocalDateTime
	 * @return String
	 */
	@Named("formatLocalDateTimeToString")
	default String formatLocalDateTimeToString(LocalDateTime dateTime) {
		return DateTransformUtil.formatLocalDateTimeToString(DateTransformUtil.DatePatternConstant.PATTERN2, dateTime);
	}

	/**
	 * 轉換物件成 JSON
	 *
	 * @param target 目標物件
	 * @return Json 字串
	 */
	@Named("serializeClassToJson")
	default String serializeClassToJson(Object target) {
		if (Objects.isNull(target)) {
			return "";
		}
		return JsonParseUtil.serialize(target);
	}

	/**
	 * 轉換 List<String> to String (以逗號分隔)
	 *
	 * @param list List<String>
	 * @return String
	 */
	@Named("transformListToString")
	default String transformListToString(List<String> list) {
		return StringArrayTransformUtil.toString(list);
	}

	/**
	 * 轉換 String (以逗號分隔) to List<String>
	 *
	 * @param target 目標字串
	 * @return List<String>
	 */
	@Named("transformStringToList")
	default List<String> transformStringToList(String target) {
		return StringArrayTransformUtil.toList(target);
	}

	/**
	 * 轉換 Set<String> to String (以逗號分隔)
	 *
	 * @param list List<String>
	 * @return String
	 */
	@Named("transformSetToString")
	default String transformSetToString(Set<String> list) {
		return StringArrayTransformUtil.toString(list);
	}

	/**
	 * 轉換 String (以逗號分隔) to Set<String>
	 *
	 * @param target 目標字串
	 * @return List<String>
	 */
	@Named("transformStringToSet")
	default Set<String> transformStringToSet(String target) {
		return StringArrayTransformUtil.toSet(target);
	}
}
