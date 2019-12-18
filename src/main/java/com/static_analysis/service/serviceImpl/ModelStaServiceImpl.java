package com.static_analysis.service.serviceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.static_analysis.mapper.tyzx.ModelStaMapper;
import com.static_analysis.service.ModelStaService;
import com.static_analysis.util.MathUtil;

@Service


public class ModelStaServiceImpl implements ModelStaService{

	protected final static Logger log = LoggerFactory.getLogger(ModelStaServiceImpl.class);
	@Autowired
	private ModelStaMapper modelStaMapper;
	@Override
	public JSONObject getModelStaData(String appName, String operateSystem,String others,String xiajiname) {
		JSONObject resultjo = new JSONObject();
		Map<String, String> map1 = new HashMap<>();
		map1.put("appName", appName);
		map1.put("operateSystem",operateSystem);
		map1.put("others", others);
		map1.put("xiajiname", xiajiname);
		List<Map<String,Object>> result = modelStaMapper.getModelStaData(map1);
		Integer sum=0;
		Double sumdouble =0.0;
		JSONArray ja =new JSONArray();
		for (Map<String, Object> map : result) {
			sum+=Integer.parseInt(map.get("y").toString());
		}
		for (int i=1;i<result.size(); i++) {
			JSONObject jo = new JSONObject();
			jo.put("name", result.get(i).get("name").toString());
			Double y =Double.parseDouble(MathUtil.AccuracyCalcMul(MathUtil.AccuracyCalcDiv(result.get(i).get("y").toString(), sum.toString(),4), "100", 2));
			sumdouble = MathUtil.AccuracyCalcAdd(sumdouble, y);
			jo.put("y", y);
			ja.add(jo);
		}
		JSONObject jo = new JSONObject();
		Double y =Double.parseDouble(MathUtil.AccuracyCalcSub("100", sumdouble.toString(),2));
		if(result.size()!=0){
			jo.put("name",result.get(0).get("name").toString());
			jo.put("y", y);
		}
		ja.add(jo);
		resultjo.put("sum", result.size());
		resultjo.put("result", ja);
		return resultjo;
	}
}
