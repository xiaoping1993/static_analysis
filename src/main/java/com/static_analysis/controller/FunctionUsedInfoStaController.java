package com.static_analysis.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.static_analysis.Enum.ResultEnum;
import com.static_analysis.entity.AjaxJson;
import com.static_analysis.entity.ComboTree;
import com.static_analysis.entity.Result;
import com.static_analysis.service.FunctionUsedInfoService;
import com.static_analysis.util.ResultUtil;

/**
 * 功能使用统计
 * @author wangjiping
 *
 */
@RestController
@RequestMapping("/functionUsedInfoSta")
public class FunctionUsedInfoStaController {
	protected final static Logger log = LoggerFactory.getLogger(FunctionUsedInfoStaController.class);
	@Autowired
	private FunctionUsedInfoService functionUsedInfoService;
	/**
	 * 获得推送点击率数据
	 * @param appName
	 * @param page
	 * @param rows
	 * @return
	 */
	@PostMapping("/getPushClickRateInfoData")
	public JSONObject getPushClickRateInfoData(String appName,Integer page,Integer rows){
		try {
			JSONObject result =  functionUsedInfoService.getPushClickRateInfoData(appName,page,rows);
			return result;
		} catch (Exception e) {
			log.error(e.toString());
			throw new RuntimeException();
		}
	}
	/**
	 * 获得报警信息
	 * @param appName
	 * @param data
	 * @return
	 */
	@RequestMapping("/getwarnDetails")
	public Result getwarnDetails(String appName,String data){
		try {
			JSONArray result =  functionUsedInfoService.getwarnDetails(appName,data);
			return ResultUtil.success(result);
		} catch (Exception e) {
			log.error(e.toString());
			return ResultUtil.error(ResultEnum.UNKONW_ERROR.getCode(), ResultEnum.UNKONW_ERROR.getMsg());
		}
	}
	/**
	 * 获得页面停留时长信息
	 * @param appName
	 * @param timing
	 * @param cidArr
	 * @param sonType
	 * @return
	 */
	@PostMapping("/getPageParktimeInfoData")
	public Result getPageParktimeInfoData(String appName,String timing,String cidArr,boolean sonType){
		try {
			JSONObject result =  functionUsedInfoService.getPageParktimeInfoData(appName,timing,cidArr,sonType);
			if(result.get("isOverLength").toString().equals("true")){//超过限制长度有提示
				return ResultUtil.error(ResultEnum.OverCidsLength.getCode(), ResultEnum.OverCidsLength.getMsg());
			}
			return ResultUtil.success(result);
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
