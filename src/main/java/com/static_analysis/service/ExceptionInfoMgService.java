package com.static_analysis.service;

import com.alibaba.fastjson.JSONObject;
import com.static_analysis.entity.Result;

public interface ExceptionInfoMgService {

	/**
	 * 获得异常信息
	 * @param appName
	 * @param operateSystem
	 * @param start
	 * @param end
	 * @param ids 
	 * @param limitend 
	 * @param limitstart 
	 * @return
	 */
	JSONObject getExceptionInfo(String appName, String state, String start, String end, String ids, Integer limitstart, Integer limitend);

	/**
	 * 处理异常问题
	 * @param id
	 * @param state
	 * @param reasons
	 * @param resolvemethod
	 * @param resolvePersons 
	 * @return
	 */
	Result doProblem(String id, String state, String reasons, String resolvemethod, String resolvePersons);

	/**
	 * 获得按错误类型分类的数据
	 * @param appName
	 * @param state
	 * @param start
	 * @param end
	 * @return
	 */
	JSONObject getExceptionInfoByType(String appName, String state, String start, String end);

	/**
	 * 获得奔溃机型柱状图统计数据
	 * @param appName
	 * @param state
	 * @param start
	 * @return
	 */
	Result getMeltModelsInfo(String appName, String start, String end);
	
}
