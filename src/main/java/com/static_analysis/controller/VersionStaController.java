package com.static_analysis.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.static_analysis.Enum.ResultEnum;
import com.static_analysis.entity.Result;
import com.static_analysis.service.VersionStaService;
import com.static_analysis.util.ResultUtil;
/**
 * 版本统计
 * @author wangjiping
 *
 */
@RestController
@RequestMapping("/versionSta")
public class VersionStaController {
	protected final static Logger log = LoggerFactory.getLogger(VersionStaController.class);
	@Autowired
	private VersionStaService versionStaService;
	/**
	 * 获得版本统计数据
	 * @param appName
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@PostMapping("/getVersionStaData")
	public Result getVersionStaData(String appName,String startTime,String endTime){
		try {
			JSONArray aa = versionStaService.getVersionStaData(appName,startTime,endTime);
			/*if(aa.size()==0){
				//假数据之后需要删除
				aa = versionStaService.getVersionStaData("1",startTime,endTime);
			}*/
			return ResultUtil.success(aa);
		} catch (Exception e) {
			log.error(e.toString());
			return ResultUtil.error(ResultEnum.UNKONW_ERROR.getCode(), ResultEnum.UNKONW_ERROR.getMsg());
		}
	}
}
