package com.static_analysis.service.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.static_analysis.Enum.OperateSystemEnum;
import com.static_analysis.mapper.tyzx.VersionStaMapper;
import com.static_analysis.service.VersionStaService;

@Service
public class VersionStaServiceImpl implements VersionStaService{

	@Autowired
	private VersionStaMapper versionStaMapper;
	@Override
	public JSONArray getVersionStaData(String appName,String startTime, String endTime) {
		JSONArray ja = new JSONArray();
		Integer size = 250;
		Integer innerSize = 100;
		for(int i=0;i<OperateSystemEnum.values().length;i++){
			JSONObject jo = new JSONObject();
			Integer operateSystem = OperateSystemEnum.values()[i].getCode();
			String operateSystemName =  OperateSystemEnum.values()[i].getName();
			Map<String,String> map1 = new HashMap<>();
			map1.put("appName", appName);
			map1.put("startTime", startTime);
			map1.put("operateSystem", operateSystem.toString());
			map1.put("endTime", endTime);
			List<Map<String, Object>> result = versionStaMapper.getVersionStaData(map1);
			List<Map<String,Object>> myResult = new ArrayList<>();//存放处理过的值
			if(result.size()==0){
				continue;
			}
			for (Map<String, Object> map : result) {
				Map<String, Object> myMap = new HashMap<>();
				myMap.put("name",operateSystemName+" "+map.get("name"));
				myMap.put("y", map.get("y"));
				myResult.add(myMap);
			}
			jo.put("name", OperateSystemEnum.values()[i].getName());
			jo.put("data", myResult);
			jo.put("size", size);
			jo.put("innerSize", innerSize);
			innerSize = size;
			size=innerSize+2*50;
			ja.add(jo);
		}
		return ja;
	}

}
