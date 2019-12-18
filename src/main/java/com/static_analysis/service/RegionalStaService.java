package com.static_analysis.service;

import com.alibaba.fastjson.JSONObject;

public interface RegionalStaService {

	/**
	 * 获得adcode下级所有<adcode,value>键值对集合
	 * @param string
	 * @param operateSystem 
	 * @param appName 
	 * @param accounts 
	 * @param cids 
	 * @return 
	 */
	JSONObject getRegionsalAdcodeValueJSON(String string, String appName, String operateSystem, String cids, String accounts);
}
