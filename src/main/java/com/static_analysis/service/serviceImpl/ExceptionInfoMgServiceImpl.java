package com.static_analysis.service.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import com.alibaba.fastjson.JSONObject;
import com.static_analysis.Enum.ResultEnum;
import com.static_analysis.entity.Result;
import com.static_analysis.mapper.tyzx.ExceptionInfoMgMapper;
import com.static_analysis.service.ExceptionInfoMgService;
import com.static_analysis.util.ResultUtil;
@Service
public class ExceptionInfoMgServiceImpl implements ExceptionInfoMgService{
	@Autowired
	private ExceptionInfoMgMapper exceptionInfoMgMapper;
	protected final static Logger log = LoggerFactory.getLogger(ExceptionInfoMgServiceImpl.class);
	@Override
	public JSONObject getExceptionInfo(String appName, String state, String start, String end,String ids,Integer limitstart,Integer limitend) {
		JSONObject jo = new JSONObject();
		try {
			Map<String,Object> map = new HashMap<>();
			map.put("appName", appName);
			map.put("state", state);
			map.put("start", start);
			map.put("end", end);
			map.put("limitstart", limitstart);
			map.put("limitend", limitend);
			map.put("ids", ids);
			Integer total = exceptionInfoMgMapper.getExceptionInfoCount(map);
			List<Map<String,Object>> result = exceptionInfoMgMapper.getExceptionInfo(map);
			jo.put("total", total);
			jo.put("rows", result);
			return jo;
		} catch (Exception e) {
			log.error(e.toString());
			throw new RuntimeException();
		}
	}
	@Override
	@PostMapping("doProblem")
	public Result doProblem(String id,String state, String reasons, String resolvemethod,String resolvePersons) {
		try {
			Integer count = exceptionInfoMgMapper.doProblem(id,state,reasons,resolvemethod,resolvePersons);
			if(count!=0){
				log.info("数据更新成功："+count+"条记录");
				return ResultUtil.success();
			}else{
				return ResultUtil.error(ResultEnum.Updatafailed.getCode(), ResultEnum.Updatafailed.getMsg());
			}
		} catch (Exception e) {
			log.error(e.toString());
			return ResultUtil.error(ResultEnum.UNKONW_ERROR.getCode(), ResultEnum.UNKONW_ERROR.getMsg());
		}
	}
	@Override
	public JSONObject getExceptionInfoByType(String appName, String state, String start, String end) {
		JSONObject ja = new JSONObject();
		try {
			Map<String,Object> map = new HashMap<>();
			map.put("appName", appName);
			map.put("state", state);
			map.put("start", start);
			map.put("end", end);
			List<Map<String,Object>> result = exceptionInfoMgMapper.getExceptionInfoByType(map);
			ja.put("total", 100);
			ja.put("rows",result);
			return ja;
		} catch (Exception e) {
			log.error(e.toString());
			throw new RuntimeException();
		}
	}
	@Override
	public Result getMeltModelsInfo(String appName,String start, String end) {
		try {
			JSONObject result = new JSONObject();
			Map<String,Object> map = new HashMap<>();
			map.put("appName", appName);
			map.put("start", start);
			map.put("end", end);
			List<Map<String,Object>> y = exceptionInfoMgMapper.getMeltModelsInfo(map);
			List<String> categories = new ArrayList<String>();
			List<Integer> series = new ArrayList<Integer>();
			for (Map<String, Object> map2 : y) {
				categories.add(map2.get("name").toString());
				series.add(Integer.parseInt(map2.get("data").toString()));
			}
			result.put("categories", categories);
			result.put("series", series);
			return ResultUtil.success(result);
		} catch (Exception e) {
			log.error(e.toString());
			return ResultUtil.error(ResultEnum.UNKONW_ERROR.getCode(), ResultEnum.UNKONW_ERROR.getMsg());
		}
	}

}
