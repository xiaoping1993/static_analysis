package com.static_analysis.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.static_analysis.Enum.ResultEnum;
import com.static_analysis.entity.Result;
import com.static_analysis.service.UsingPeriodsService;
import com.static_analysis.util.ResultUtil;
/**
 * 用户使用时段统计
 * @author wangjiping
 *
 */
@RestController
@RequestMapping("/usingPeriodsSta")
public class UsingPeriodsController {
	protected static final Logger log = LoggerFactory.getLogger(UsingPeriodsController.class);
	@Autowired
	private UsingPeriodsService usingPeriodsService;
	/**
	 * 获得用户使用时段数据
	 * @param appName
	 * @param timeParticle
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("getUsingPeriodsData")
	public Result getUsingPeriodsData(String appName,String timeParticle,String start,String end){
		try {
			return usingPeriodsService.getUsingPeriodsInfo(appName,timeParticle,start,end);
		} catch (Exception e) {
			log.error(e.toString());
			return ResultUtil.error(ResultEnum.UNKONW_ERROR.getCode(), ResultEnum.UNKONW_ERROR.getMsg());
		}
	}
}
