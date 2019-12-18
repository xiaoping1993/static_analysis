package com.static_analysis.service;

import com.alibaba.fastjson.JSONArray;

public interface VersionStaService {
	public JSONArray getVersionStaData(String appName,String systemLx, String startTime);
}
