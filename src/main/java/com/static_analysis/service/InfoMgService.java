package com.static_analysis.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import com.static_analysis.entity.ComboTree;

public interface InfoMgService {

	/**
	 * 获得所有菜单信息
	 * @param end 
	 * @param start 
	 * @return
	 */
	List<Map<String, Object>> getAllMenus(int start, int end);

	/**
	 * 获得所有菜单总数
	 * @return
	 */
	int getAllMenusCount();

	/**
	 * 菜单信息保存
	 * @param pid
	 * @param pid2 
	 * @param name
	 * @param url
	 * @param request 
	 * @return
	 */
	boolean menusSave(HttpServletRequest request,Integer order,Integer id,Integer pid, String name, String url,Boolean isEdit);

	/**
	 * 删除菜单项
	 * @param id
	 * @param request 
	 * @return
	 */
	boolean menusDele(Integer id, HttpServletRequest request);

	/**
	 * 获得pid异步树
	 * @param id
	 */
	List<ComboTree> getAllmenusJSONTree(String id);

}
