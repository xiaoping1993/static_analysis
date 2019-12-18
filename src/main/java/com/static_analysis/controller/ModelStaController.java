package com.static_analysis.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.static_analysis.Enum.ResultEnum;
import com.static_analysis.entity.Result;
import com.static_analysis.service.ModelStaService;
import com.static_analysis.util.ResultUtil;

/**
 * 机型统计
 * @author wangjiping
 *
 */
@RestController
@RequestMapping("/modelSta")
public class ModelStaController {
	protected final static Logger log = LoggerFactory.getLogger(ModelStaController.class);
	@Autowired
	private ModelStaService modelStaService;
	
	/**
	 * 获得机型，品牌数据
	 * @param appName
	 * @param operateSystem
	 * @param others
	 * @return
	 */
	@PostMapping("/getModelStaData")
	public Result getModelStaData(String appName,String operateSystem,String others,String xiajiname){
		try {
			JSONObject jo = modelStaService.getModelStaData(appName,operateSystem,others,xiajiname);
			Integer sum =jo.getIntValue("sum");
			if(sum==0){
				return ResultUtil.error(ResultEnum.EmptyData.getCode(), ResultEnum.EmptyData.getMsg());
			}
			JSONArray ja = jo.getJSONArray("result");
			return ResultUtil.success(ja);
		} catch (Exception e) {
			log.error(e.toString());
			return ResultUtil.error(ResultEnum.UNKONW_ERROR.getCode(), ResultEnum.UNKONW_ERROR.getMsg());
		}
		
	}
}
