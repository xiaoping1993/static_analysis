package com.static_analysis.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSONObject;
import com.static_analysis.Enum.ResultEnum;
import com.static_analysis.entity.AjaxJson;
import com.static_analysis.entity.ComboTree;
import com.static_analysis.entity.Result;
import com.static_analysis.service.ActivitydegreeStaService;
import com.static_analysis.service.FunctionUsedInfoService;
import com.static_analysis.util.ResultUtil;

/**
 * 活跃度统计
 * @author wangjiping
 *
 */
@RestController
@RequestMapping("/activitydegreeSta")
public class ActivitydegreeStaController {
	protected final static Logger log = LoggerFactory.getLogger(ActivitydegreeStaController.class);
	@Autowired
	private ActivitydegreeStaService activitydegreeStaService;
	@Autowired
	private FunctionUsedInfoService functionUsedInfoService;
	/**
	 * 获得活跃度统计数据
	 * @param appname
	 * @param operateSystem
	 * @param appVersion
	 * @param timing
	 * @return
	 */
	@PostMapping("/getAreaData")
	public Result getAreaData(String appname,String operateSystem,String appVersion,String timing,String cids,String accounts){
		try {
			JSONObject ja = activitydegreeStaService.getAreaData(appname,operateSystem,appVersion,timing,cids,accounts);
			return ResultUtil.success(ja);
		} catch (Exception e) {
			log.error(e.toString());
			return ResultUtil.error(ResultEnum.UNKONW_ERROR.getCode(), ResultEnum.UNKONW_ERROR.getMsg());
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
