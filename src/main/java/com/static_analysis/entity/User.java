package com.static_analysis.entity;

/**
 * 用户类
 * @author wangjiping
 *
 */
public class User {
	private String name;
	private Integer role_id;
	private String menus_ids;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getRole_id() {
		return role_id;
	}
	public void setRole_id(Integer role_id) {
		this.role_id = role_id;
	}
	public String getmenus_ids() {
		return menus_ids;
	}
	public void setRole_id(String menus_ids) {
		this.menus_ids = menus_ids;
	}
}
