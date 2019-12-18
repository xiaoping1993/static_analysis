package com.static_analysis.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSONObject;
import com.static_analysis.entity.AjaxJson;
import com.static_analysis.entity.ComboTree;
import com.static_analysis.service.DispatchStaService;
import com.static_analysis.service.FunctionUsedInfoService;

@RestController
@RequestMapping("dispatchSta")
public class DispatchStaController {
	protected final static Logger log = Logger.getLogger(DispatchStaController.class);
	@Autowired
	private DispatchStaService dispatchService;
	@Autowired
	private FunctionUsedInfoService functionUsedInfoService;
	@RequestMapping("getLarryStaData")
	public JSONObject getLarryStaData(String cid,String startTime,String endTime,int page,int rows){
		try {
			int start = (page-1)*rows;
			int end = rows;
			JSONObject result = dispatchService.getLarryStaData(cid,startTime,endTime,start,end);
			return result;
		} catch (Exception e) {
			log.error(e.toString());
			throw new RuntimeException();
		}
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
