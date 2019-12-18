package com.static_analysis.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.static_analysis.Enum.ResultEnum;
import com.static_analysis.entity.AjaxJson;
import com.static_analysis.entity.Result;
import com.static_analysis.service.MainService;
import com.static_analysis.util.ResultUtil;

/**
 * 主页面
 * @author wangjiping
 *
 */
@RestController
@RequestMapping("/main")
public class MainController {
	protected static final Logger log = LoggerFactory.getLogger(MainController.class);
	@Autowired
	private MainService mainService;
	
	/**
	 * 主页面内部跳转
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(params = "toMain")
	public ModelAndView toMain(HttpServletRequest request, HttpServletResponse response) {
		return new ModelAndView("/main/index");
	}

	/**
	 * 获得指定menus_ids的菜单栏信息
	 * @param menus_ids
	 * @return
	 */
	@RequestMapping(params = "getMenusInfo")
	public Result getMenusInfo(String menus_ids) {
		try {
			menus_ids += "-1";
			List<Map<String, Object>> list = mainService.getMenusInfo(menus_ids);
			JSONArray list1 = new JSONArray();
			list1.addAll(list);
			return ResultUtil.success(list1);
		} catch (Exception e) {
			return ResultUtil.error(ResultEnum.UNKONW_ERROR.getCode(), ResultEnum.UNKONW_ERROR.getMsg());
		}
	}

	// 提供给手机段发送数据的接口
	@RequestMapping("postLoginInfo")
	public AjaxJson postLoginInfo(@RequestParam(name="mphone_imei")String mphone_imei, @RequestParam(name="mphone_brand")String mphone_brand, @RequestParam(name="mphone_mode")String mphone_mode, @RequestParam(name="version")String version,
			@RequestParam(name="longitude")String longitude, @RequestParam(name="latitude")String latitude, @RequestParam(name="exception_msg")String exception_msg,@RequestParam(name="cid",required=false)String cid,  @RequestParam(name="cidName",required=false)String cidName, @RequestParam(name="loginName")String loginName, @RequestParam(name="appName")Integer appName,
			@RequestParam(name="operateSystem")Integer operateSystem,@RequestParam(value = "jsonpCallback" ,required=false)String jsonpCallback, HttpServletResponse response) {
		AjaxJson result =  mainService.postLoginInfo(mphone_imei, mphone_brand, mphone_mode, version, longitude, latitude,
				exception_msg,cid,cidName, loginName, appName, operateSystem);
		if(jsonpCallback!=null){
			jsonpWriter(jsonpCallback, result, response);
			return null;
		}
		return result;
	}

	/**
	 * 接收客户端点击了的推送消息
	 * 
	 * @return
	 */
	@RequestMapping("postPushMessageInfo")
	public AjaxJson postPushMessageInfo(@RequestParam(name="appName")String appName, @RequestParam(name="warnType")String warnType,@RequestParam(name="messageId")String messageId,@RequestParam(name="reciptTime")String reciptTime) {
		try {
			return mainService.postPushMessageInfo(appName, warnType,messageId,reciptTime);
		} catch (Exception e) {
			log.error(e.toString());
			throw new RuntimeException();
		}
	}

	/**
	 * 接收客户端发送来的页面信息
	 * 
	 * @param mphone_imei
	 * @param appName
	 * @param pageCode
	 * @return
	 */
	@RequestMapping("postPageInfo")
	public AjaxJson postPageInfo(@RequestParam(name="mphone_imei")String mphone_imei, @RequestParam(name="appName")String appName, @RequestParam(name="pageCode")String pageCode, @RequestParam(name="loginName")String loginName,
			@RequestParam(name="cid")String cid,@RequestParam(name="cidName")String cidName,@RequestParam(value = "jsonpCallback" ,required=false)String jsonpCallback,HttpServletResponse response) {
		try {
			AjaxJson result = mainService.postPageInfo(pageCode, mphone_imei, appName, loginName,cid,cidName);
			if(jsonpCallback!=null){
				jsonpWriter(jsonpCallback, result, response);
				return null;
			}
			return result;
		} catch (Exception e) {
			log.error(e.toString());
			return null;
		}
	}

	/**
	 * 接口客户端发来的异常信息
	 * @return
	 */
	@RequestMapping("postErrorInfo")
	public AjaxJson postErrorInfo(@RequestParam(name="mphone_imei")String mphone_imei, @RequestParam(name="appName")String appName,@RequestParam(name="type")String type, @RequestParam(name="exception_msg")String exception_msg,
			@RequestParam(name="exception_detailmsgString")String exception_detailmsgString, @RequestParam(name="exception_detailmsgFile")MultipartFile exception_detailmsgFile) {
		try {
			return mainService.postExceptionInfo(mphone_imei, appName,type, exception_msg, exception_detailmsgString,
					exception_detailmsgFile);
		} catch (Exception e) {
			log.error(e.toString());
			throw new RuntimeException();
		}
	}

	/**
	 * 返回客户端登陆用户信息
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(params = "searchUserInfo")
	public Result searchUserInfo(HttpServletRequest request) {
		HttpSession session = request.getSession();
		Object loginName = session.getAttribute("loginName");
		Object menus_ids = session.getAttribute("menus_ids");
		if (loginName == null) {
			return ResultUtil.error(ResultEnum.ClientLoginFailure.getCode(), ResultEnum.ClientLoginFailure.getMsg());
		}
		JSONObject result = new JSONObject();
		result.put("loginName", loginName.toString());
		result.put("menus_ids", menus_ids.toString());
		return ResultUtil.success(result);
	}
	protected void jsonpWriter(String jsonpCallback, AjaxJson j, HttpServletResponse response) {
		response.setContentType("text/plain");  
        try {  
        	 response.getWriter().write(jsonpCallback + "("+j.getJsonStr()+ ")");   
        } catch (IOException e) {  
             e.printStackTrace();  
        }
	}
	/**
	 * 清除session
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(params = "clearSession")
	public Result clearSession(HttpServletRequest request) {
		HttpSession session = request.getSession();
		session.invalidate();
		return ResultUtil.success();
	}
}
