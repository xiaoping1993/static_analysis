package com.static_analysis.mapper.tyzx;

import java.util.List;
import java.util.Map;
public interface ActivitydegreeStaMapper {
	/**
	 * 获得活跃度统计数据
	 * @param otherDates 
	 * @param appName
	 * @param operateSystem
	 * @param appVersion
	 * @param timing
	 * @return
	 */
	List<String> getActivitys(Map<String, Object> map);
	/**
	 * 获得每日新增用户数
	 * @param map
	 * @return
	 */
	List<Integer> getAddPersons(Map<String,Object> map);
	/**
	 * 获得总人数
	 * @param appVersion 
	 * @param operateSystem 
	 * @param appName 
	 * @param map
	 * @return
	 */
	Integer getTotalPersons(Map<String, Object> map);
}
