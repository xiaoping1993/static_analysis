package com.static_analysis.service.serviceImpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.static_analysis.entity.AjaxJson;
import com.static_analysis.entity.ComboTree;
import com.static_analysis.mapper.gpsdb.FunctionUsedInfoGpsdbMapper;
import com.static_analysis.mapper.tyzx.FunctionUsedInfoMapper;
import com.static_analysis.service.FunctionUsedInfoService;
import com.static_analysis.util.MathUtil;

@Service
public class FunctionUsedInfoServiceImpl implements FunctionUsedInfoService{

	protected final static Logger log = LoggerFactory.getLogger(FunctionUsedInfoServiceImpl.class);
	@Autowired
	private FunctionUsedInfoMapper functionUsedInfoMapper;
	@Autowired
	private FunctionUsedInfoGpsdbMapper functionUsedInfoGpsdbMapper;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private final static Integer cidslengthlimit=10;//最多展现客户数
	@Override
	public JSONObject getPushClickRateInfoData(String appName,Integer page,Integer rows) {
		JSONObject jo = new JSONObject();
		Integer start = (page-1)*rows+1;
		Integer end = start+rows-1;
		Map<String,Object> map = new HashMap<>();
		map.put("appName", appName);
		map.put("start", start);
		map.put("end", end);
		Integer total = functionUsedInfoMapper.getPushClickRateInfoTotal(map);
		List<Map<String,Object>> result = functionUsedInfoMapper.getPushClickRateInfoData(map);
		//重置result添加rate点击率
		for (int i = 0; i < result.size(); i++) {
			Map<String,Object> map1 =  result.get(i);
			String pushTotals = map1.get("pushtotals").toString();
			String clickTotals = map1.get("clicktotals").toString();
			String rate = "0";
			if(!clickTotals.equals("0")){
				rate = MathUtil.AccuracyCalcDiv(clickTotals,pushTotals, 2);
			}
			map1.put("rate", rate);
			result.set(i, map1);
		}
		jo.put("total", total);
		jo.put("rows", result);
		return jo;
	}
	/**
	 * 获取客户信息
	 */
	@Override
	public List<ComboTree> cusTree4All(Integer targetCid, Integer targetOid,String customerPidSql) {
		List<Map<String, Object>> customerList = functionUsedInfoGpsdbMapper.cusTree4All(targetCid,targetOid,customerPidSql);
		List<ComboTree> comboTrees = new ArrayList<ComboTree>();
		Map<String, Object> map = new HashMap<String, Object>();
		for (int i = 0; i < customerList.size(); i++) {
			map = customerList.get(i);
			ComboTree comboTreeTemp = new ComboTree();
			String customerId = String.valueOf(map.get("id"));
			comboTreeTemp.setId("c_"+customerId);
			comboTreeTemp.setAccount(String.valueOf(map.get("accounts")));
			comboTreeTemp.setText(String.valueOf(map.get("name")));
			if("0".equals(String.valueOf(map.get("childs")))){
				comboTreeTemp.setState("open");
			}else{
				comboTreeTemp.setState("closed");
			}
			comboTrees.add(comboTreeTemp);
		}
		return comboTrees;
	}
	@Override
	public AjaxJson searchCus4All(String search, String cid) {
		AjaxJson j = new AjaxJson();
		Map<String,Object> taretCustomer = functionUsedInfoGpsdbMapper.getCustomerById(cid);
		try {
			String path = taretCustomer.get("path").toString();
			String id = taretCustomer.get("id").toString();
			List<Map<String, Object>> customerList = functionUsedInfoGpsdbMapper.getCustomerList(path,id,search);
			j.setSuccess(true);
			j.setObj(customerList);
		} catch(Exception e) {
			e.printStackTrace();
			j.setSuccess(false);
			j.setMsg("操作失败！");
			j.setObj(null);
		}
		return j;
	}
	@Override
	public JSONArray getwarnDetails(String appName, String data) {
		JSONArray ja = new JSONArray();
		Integer sum = 0;
		List<Map<String,Object>> result =  functionUsedInfoMapper.getwarnDetails(appName,data);
		for (Map<String, Object> map : result) {
			sum+=Integer.parseInt(map.get("sum").toString());
		}
		
		Double piesum = Double.parseDouble("0");
		Integer length = result.size();
		for(int i = 0;i<length-1;i++){
			JSONObject jo = new JSONObject();
			Double y = Double.parseDouble(MathUtil.AccuracyCalcDiv(result.get(i).get("sum").toString(),sum.toString(), 3));
			jo.put("name", result.get(i).get("name").toString());
			jo.put("y", y);
			ja.add(jo);
			piesum+=y;
		}
		
		//将最后一个存入
		Double others =Double.parseDouble(MathUtil.AccuracyCalcSub("1",piesum.toString(), 3));
		JSONObject jo1 = new JSONObject();
		jo1.put("name",result.get(length-1).get("name").toString());
		jo1.put("y", others);
		ja.add(jo1);
		return ja;
	}
	@Override
	public JSONObject getPageParktimeInfoData(String appName, String timing, String cidArr, boolean sonType) {
		JSONObject result = new JSONObject();
		result.put("isOverLength", "false");
		//获得前cidslengthlimit个客户cids
		String[] cidA = (cidArr+",").split(",");
		List<String> cids = new ArrayList<String>(Arrays.asList(cidA));
		Integer length = cids.size();
		if(length>cidslengthlimit){
			result.put("isOverLength", "true");
			return result;
		}
		if(sonType){
			String cidstring = "(";
			for (int i = 0; i < length-1; i++) {
				cidstring+=cids.get(i)+",";
			}
			cidstring+=cids.get(length-1)+")";
			cids = functionUsedInfoGpsdbMapper.getCpidsByids(cidstring);
			if(cids.size()>cidslengthlimit){
				result.put("isOverLength", "true");
				return result;
			}
		}
		String cidsString ="(";
		Integer newLength = cids.size();
		for (int i = 0; i < newLength-1; i++) {
			cidsString+=cids.get(i)+",";
		}
		cidsString+=cids.get(newLength-1)+")";
		List<String> x = functionUsedInfoGpsdbMapper.getCidNamesBycids(cidsString);//获得需要展现的客户名称
		JSONArray y = new JSONArray();//存储一个页面在所有客户对应的停留时长对应前台展示的y轴数据
		List<Map<String,Object>> focusPages = functionUsedInfoMapper.getfocusPages(appName);
		//给y赋值
		for (Map<String,Object> focusPage : focusPages) {
			JSONObject jo = new JSONObject();
			jo.put("name", focusPage.get("pagename"));//存储页面名称
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("cidsString", cidsString);
			map.put("cids", cids);
			map.put("timing", timing);
			map.put("pageCode", focusPage.get("pagecode").toString());
			List<Integer> pageParkingtimes = functionUsedInfoMapper.getPageParkingtimes(map);//获得页面对应所有cid的停留时长
			jo.put("data", pageParkingtimes);
			y.add(jo);
		}
		result.put("categories", x);
		result.put("series", y);
		return result;
	}
}
