package com.static_analysis.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.static_analysis.entity.Result;

public interface LoginService {
	/**
	 * 根据用户名查密码
	 * @param loginName
	 * @return
	 */
	String getUserPwByName(String loginName);
	/**
	 * 修改密码
	 * @param loginName
	 * @param newPw
	 * @return 
	 */
	Integer savePw(String loginName, String newPw);
	/**
	 * 检查用户登陆
	 * @param request 
	 * @param response 
	 * @param name
	 * @param password
	 * @return
	 */
	Result checkLogin(HttpServletRequest request, HttpServletResponse response, String name, String password);
	/**
	 * 刷脸登陆：先对图片质量检测；质量通过后将将图片注册到百度AI中
	 * @param loginName
	 * @param imgBase64
	 * @return
	 */
	Result faceLogin(String loginName, MultipartFile imgBase64);
	/**
	 * 获得系统条件参数appName,系统类型,version
	 * @return
	 */
	JSONObject getConditionParams();
}
