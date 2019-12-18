package com.static_analysis.service.serviceImpl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONObject;
import com.static_analysis.mapper.tyzx.DispatchMapper;
import com.static_analysis.service.DispatchStaService;

@Service
public class DispatchServiceImpl implements DispatchStaService {

	protected final static Logger log = LoggerFactory.getLogger(DispatchServiceImpl.class);
	@Autowired
	private DispatchMapper dispatchMapper;
	@Override
	public JSONObject getLarryStaData(String cid, String startTime, String endTime,int start,int end) {
		try {
			JSONObject larryData = new JSONObject();
			int total = dispatchMapper.getLarryStaDataCounts(cid,startTime,endTime);
			List<Map<String,Object>> result = dispatchMapper.getLarryStaData(cid,startTime,endTime,start,end);
			larryData.put("total", total);
			larryData.put("rows", result);
			return larryData;
		} catch (Exception e) {
			log.error(e.toString());
			throw new RuntimeException();
		}
	}
	@Override
	public void doLarryToolSta(String yesterday) {
		//统计微信小工具的使用情况（ty_wx_install_record中获取数据）
		
		//统计通过派工平台的使用情况（p_order中获取数据）
	}

}
