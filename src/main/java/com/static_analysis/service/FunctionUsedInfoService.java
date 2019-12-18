package com.static_analysis.service;

import java.util.List;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.static_analysis.entity.AjaxJson;
import com.static_analysis.entity.ComboTree;
public interface FunctionUsedInfoService {

	JSONObject getPushClickRateInfoData(String appName,Integer page,Integer rows);

	List<ComboTree> cusTree4All(Integer targetCid, Integer targetOid, String customerPidSql);

	AjaxJson searchCus4All(String search, String cid);

	/**
	 * 获得功能统计中报警信息
	 * @param appName
	 * @param data
	 * @return
	 */
	JSONArray getwarnDetails(String appName, String data);

	/**
	 * 获取页面停留时长信息
	 * @param appName
	 * @param timing
	 * @param cidArr
	 * @param sonType
	 * @return
	 */
	JSONObject getPageParktimeInfoData(String appName, String timing, String cidArr, boolean sonType);

}
