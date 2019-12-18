package com.static_analysis.service;

import com.alibaba.fastjson.JSONObject;

public interface DispatchStaService {

	/**
	 * 获得对应客户下拉车工具使用情况
	 * @param cid
	 * @param startTime
	 * @param endTime
	 * @param end 
	 * @param start 
	 * @return
	 */
	JSONObject getLarryStaData(String cid, String startTime, String endTime, int start, int end);

	/**
	 * 统计一天中每个客户所使用微信小工具，派工平台的情况
	 * @param yesterday
	 */
	void doLarryToolSta(String yesterday);
	
}
