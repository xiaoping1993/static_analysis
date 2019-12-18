package com.static_analysis.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.static_analysis.entity.ComboTree;
import com.static_analysis.service.InfoMgService;

/**
 * 信息管理
 * @author wangjiping
 *
 */
@RestController
@RequestMapping("/infoMg")
public class InfoMgController {
	protected static final Logger log = LoggerFactory.getLogger(InfoMgController.class);
	@Autowired
	private InfoMgService infoMgService;
	/**
	 * 获得所有菜单栏信息
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("menus/getAllMenus")
	public JSONObject getAllMenus(int page,int rows){
		JSONObject result = new JSONObject();
		result.put("total", 0);
		try {
			int count = infoMgService.getAllMenusCount();
			int start = (page-1)*rows;
			int end = rows;
			List<Map<String,Object>> allMenus = infoMgService.getAllMenus(start,end);
			result.put("rows", allMenus);
			result.put("total", count);
			return result;
		} catch (Exception e) {
			log.error(e.toString());
			throw new RuntimeException();
		}
	}
	/**
	 * 报错添加的或修的菜单栏信息
	 * @param request
	 * @param order
	 * @param id
	 * @param pid
	 * @param name
	 * @param url
	 * @param isEdit
	 * @return
	 */
	@PostMapping("menus/menusSave")
	public boolean menusSave(HttpServletRequest request,Integer order,Integer id,Integer pid,String name,String url,Boolean isEdit){
		return infoMgService.menusSave(request,order,id,pid,name,url,isEdit);
	}
	/**
	 * 删除指定菜单栏
	 * @param id
	 * @param request
	 * @return
	 */
	@PostMapping("menus/menusDele")
	public boolean menusDele(Integer id,HttpServletRequest request){
		return infoMgService.menusDele(id,request);
	}
	/**
	 * 获得菜单栏树状图jsontree数据
	 * @param id
	 * @return
	 */
	@RequestMapping("menus/getAllmenusJSONTree")
	public List<ComboTree> getAllmenusJSONTree(String id){
		try {
			return infoMgService.getAllmenusJSONTree(id);
		} catch (Exception e) {
			log.error(e.toString());
			throw new RuntimeException();
		}
	}
}
