package com.static_analysis.service;

import com.alibaba.fastjson.JSONObject;

public interface ModelStaService {

	/**
	 * 机型统计数据
	 * @param others 
	 * @param xiajiname 
	 */
	JSONObject getModelStaData(String appName, String operateSystem, String others, String xiajiname);

}
