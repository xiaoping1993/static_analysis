package com.static_analysis.service.serviceImpl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONObject;
import com.static_analysis.mapper.tyzx.RegionalStaMapper;
import com.static_analysis.service.RegionalStaService;

@Service
public class RegionalStaServiceImpl implements RegionalStaService{

	protected final static Logger log = LoggerFactory.getLogger(RegionalStaServiceImpl.class);
	@Autowired
	private RegionalStaMapper regionalStaMapper;
	@Override
	public JSONObject getRegionsalAdcodeValueJSON(String adcode,String appName,String operateSystem,String cids,String accounts) {
		boolean flag = false;
		JSONObject result = new JSONObject();
		cids=(cids.equals("''")?"":cids);
		//通过customer获得对应的账户
		if(adcode.equals("100000")){//国下级省
			List<Map<String,Object>> provinces = regionalStaMapper.getRegionsalAdcodeXiajiShengValueJSON(appName,operateSystem,cids,accounts);
			for (Map<String, Object> map : provinces) {
				if(!map.get("count").toString().equals("0")){
					flag = true;
				}
				result.put(map.get("code").toString(), map.get("count").toString());
			}
		}else{//省下级
			Integer myAdcode = Integer.parseInt(adcode.substring(0,2));
			if(myAdcode==11||myAdcode==12||myAdcode==11||myAdcode==31||myAdcode==50||myAdcode==81||myAdcode==82){//下级是区
				List<Map<String,Object>> districts = regionalStaMapper.getRegionsalXiajiQuAdcodeValueJSON(adcode,myAdcode+"%",appName,operateSystem,cids,accounts);
				for (Map<String, Object> map : districts) {
					if(!map.get("count").toString().equals("0")){
						flag = true;
					}
					result.put(map.get("code").toString(), map.get("count").toString());
				}
			}else{//下级是市
				List<Map<String,Object>> citys = regionalStaMapper.getRegionsalXiajiShiAdcodeValueJSON(adcode,myAdcode+"%",appName,operateSystem,cids,accounts);
				for (Map<String, Object> map : citys) {
					if(!map.get("count").toString().equals("0")){
						flag = true;
					}
					result.put(map.get("code").toString(), map.get("count").toString());
				}
			}
		}
		result.put("flag", flag);
		return result;
	}
}
