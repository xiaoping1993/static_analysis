package com.static_analysis.service;

import com.alibaba.fastjson.JSONObject;

public interface ActivitydegreeStaService {

	/**
	 * 获得活跃度统计数据
	 * @param appName
	 * @param operateSystem
	 * @param appVersion
	 * @param timing
	 * @param accounts 
	 * @param cids 
	 * @return
	 */
	JSONObject getAreaData(String appName, String operateSystem, String appVersion, String timing, String cids, String accounts);
}
