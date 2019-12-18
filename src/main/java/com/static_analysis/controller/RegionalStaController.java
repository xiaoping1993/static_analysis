package com.static_analysis.controller;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSONObject;
import com.static_analysis.entity.AjaxJson;
import com.static_analysis.entity.ComboTree;
import com.static_analysis.service.FunctionUsedInfoService;
import com.static_analysis.service.RegionalStaService;

/**
 * 地域统计
 * @author wangjiping
 *
 */
@RestController
@RequestMapping("/regionalSta")
public class RegionalStaController {
	protected static final Logger log = LoggerFactory.getLogger(RegionalStaController.class);
	@Autowired
	private RegionalStaService regionsalStaService;
	@Autowired
	private FunctionUsedInfoService functionUsedInfoService;
	@Value("${config.gaode.key}")
	private String gaodeKey;
	
	@RequestMapping("/getRegionsalAdcodeValueJSON")
	/**
	 * 获得地区对应的adcode,value集合
	 * @param regionsalAdcode 地区对应的adcode
	 * @return
	 */
	public JSONObject getRegionsalAdcodeValueJSON(String regionsalAdcode,String appName,String operateSystem,String cids,String accounts){
		return  regionsalStaService.getRegionsalAdcodeValueJSON(regionsalAdcode,appName,operateSystem,cids,accounts);
	}
	/**
	 * 获取客户信息
	 */
	@RequestMapping("/cusTree4All")
	public List<ComboTree> cusTree4All(String id,Integer targetCid,Integer targetOid){
		try{
			String customerPidSql = "";
			if (id != null) {
				String cid = id.split("_")[1];
				customerPidSql = " a.pid = " + cid;
			}else{
				int cid = 1;
				customerPidSql = " a.id= " + cid;
			}
			List<ComboTree> result = functionUsedInfoService.cusTree4All(targetCid,targetOid,customerPidSql);
			return result;
		}catch(Exception e){
			log.error(e.toString());
			throw new RuntimeException();
		}
	}
	
	/**
	 * 过滤查询
	 * 
	 * @param gid
	 * @param request
	 * @return
	 */
	@RequestMapping("searchCus4All")
	public AjaxJson searchCus4All(
			String search,
			String cid, 
			HttpServletRequest request) {
		try{
			return functionUsedInfoService.searchCus4All(search,cid);
		}catch(Exception e){
			log.error(e.toString());
			throw new RuntimeException();
		}
	}
}
