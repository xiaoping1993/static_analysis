package com.static_analysis.service;

import com.static_analysis.entity.Result;

public interface UsingPeriodsService {

	/**
	 * 获得前一天的精确到10min的各个时段正在使用的用户数（这里的用户是指操作人loginname+mphoneimei）
	 * @param data
	 */
	void doUserUsedtimestamp(String data);

	/**
	 * 获得用户使用时长信息
	 * @param appName
	 * @param timeParticle
	 * @param start
	 * @param end
	 * @return
	 */
	Result getUsingPeriodsInfo(String appName, String timeParticle, String start, String end);
}
