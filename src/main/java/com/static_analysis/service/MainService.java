package com.static_analysis.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.static_analysis.entity.AjaxJson;

public interface MainService {

	/**
	 * 获得menus_ids对应的详细信息
	 * @param string
	 * @return
	 */
	List<Map<String, Object>> getMenusInfo(String string);

	/**
	 * 将从软件登陆获得的数据存入数据库
	 * @param mphone_imei
	 * @param mphone_mode
	 * @param version
	 * @param longitude
	 * @param latitude
	 * @param exception_msg
	 * @param loginName
	 * @param loginName2 
	 * @param cid
	 * @param cidName 
	 * @param appName
	 * @param operateSystem
	 * @return 
	 */
	AjaxJson postLoginInfo(String mphone_imei,String mphone_brand, String mphone_mode, String version, String longitude, String latitude,
			String exception_msg,String cid,String cidName, String loginName, Integer appName, Integer operateSystem);

	/**
	 * 处理点击的推送消息
	 * @param appName
	 * @param warnType
	 * @param reciptTime 
	 * @param cid 
	 * @return
	 */
	AjaxJson postPushMessageInfo(String appName, String warnType, String messageId, String reciptTime);
	
	/**
	 * 汇总time日期的推送数据
	 */
	void doPushInfo(String time);

	/**
	 * 处理发送来的页面停留信息
	 * @param mphone_imei
	 * @param appName
	 * @param pageCode
	 * @param cidName 
	 * @param cid 
	 * @return
	 */
	AjaxJson postPageInfo(String pageCode,String mphone_imei, String appName, String loginName, String cid, String cidName);

	/**
	 * 汇总time日期的停留时间
	 * @param time 
	 */
	void doPageParkingtime(String time);

	/**
	 * 处理终端错误信息
	 * @param mphone_imei
	 * @param appName
	 * @param appName2
	 * @param appVersion
	 * @param exception_detailmsg
	 * @return
	 */
	AjaxJson postExceptionInfo(String mphone_imei, String appName,String type ,String exception_msg, String exception_detailmsgString, MultipartFile exception_detailmsg);

	/**
	 * 收集昨天一天的服务端错误日志
	 * @param yesterday
	 */
	void doErrorLog(String yesterday);

}
