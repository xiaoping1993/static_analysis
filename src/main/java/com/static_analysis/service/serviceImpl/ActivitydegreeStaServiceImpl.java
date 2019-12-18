package com.static_analysis.service.serviceImpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONObject;
import com.static_analysis.mapper.tyzx.ActivitydegreeStaMapper;
import com.static_analysis.service.ActivitydegreeStaService;

@Service
public class ActivitydegreeStaServiceImpl implements ActivitydegreeStaService{
	protected final static Logger log = LoggerFactory.getLogger(ActivitydegreeStaServiceImpl.class);
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	@Autowired
	private ActivitydegreeStaMapper activitydegreeStaMapper;
	@Override
	public JSONObject getAreaData(String appName, String operateSystem, String appVersion, String timing,String cids,String accounts) {
		JSONObject jo = new JSONObject();
		Integer time = Integer.parseInt(timing);
		//创建timing时间间隔内所有的日期List
		List<String> otherDates = new ArrayList<>();
		Date now = new Date();
		Calendar ca = Calendar.getInstance();
		ca.setTime(now);
		ca.add(Calendar.DATE, -time+1);
		Date startDate = ca.getTime();
		otherDates.add(sdf.format(startDate));
		for(int i=1;i<time;i++){
			ca.add(Calendar.DATE, 1);
			Date date = ca.getTime();
			otherDates.add(sdf.format(date.getTime()));
		}
		cids = (cids.equals("''")?"":cids);
		//创建日期list结束
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("appName", appName);
		map.put("operateSystem",operateSystem);
		map.put("appVersion", appVersion);
		map.put("timing", timing);
		map.put("otherDates", otherDates);
		map.put("cids", cids);
		map.put("accounts", accounts);
		List<String> activitys = activitydegreeStaMapper.getActivitys(map);
		List<Integer> addPersons = activitydegreeStaMapper.getAddPersons(map);
		Integer totalPersons = activitydegreeStaMapper.getTotalPersons(map);
		LinkedList<Integer> personsList =new LinkedList<Integer>();
		LinkedList<Integer> personsList1 =new LinkedList<Integer>();
		Integer personSum = totalPersons;//存每日当天总人数
		personsList.add(totalPersons);
		//addPersons,totalPersons变为每日总人数personsList
		for (int i = addPersons.size()-1; i >= 1; i--) {
			personSum = personSum - addPersons.get(i);
			personsList.add(personSum);
		}
		//按存入属性倒序排序
		Iterator<Integer> it = personsList.iterator();
		while(it.hasNext()){
			personsList1.addFirst(it.next());
		}
		jo.put("addPersons", addPersons);
		jo.put("personsList", personsList1);
		jo.put("activitys", activitys);
		jo.put("totalPersons", totalPersons);
		return jo;
	}
}
