package com.static_analysis.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;

import com.static_analysis.entity.Result;

public interface UserMgService {
	
	public List<Map<String,Object>> findAllUser(int page, int rows);

	public int getAllUserCounts();

	public int resetPwd(Integer userId, String userName);

	public int deleUser(Integer userId);

	public List<Map<String, Object>> getRoles();

	public List<Integer> getRoleIds(Integer userId);

	public Integer insertUserRoles(List<Integer> id, Integer userId);
	
	public Integer insertUserRole(Integer id, Integer userId);

	public String getUserIdByUserName(String userName);
	
	/**
	 * 获得所有没有分配用户角色的用户对象
	 * @return
	 */
	public List<Map<String,Object>> getAllUserNoRole();

	/**
	 * 获得所有权限
	 */
	public List<Map<String,Object>> getAuthoritys();

	/**
	 * 根据角色id获得对应的权限
	 * @param request 
	 * @return
	 */
	public List<Map<String, Object>> getAuthoritysByRole(String roleId);

	/**
	 * 为角色分配权限
	 * @param roleId
	 * @param list
	 * @param request 
	 */
	public boolean configRoleAuthoritys(String roleId, String[] list, HttpServletRequest request);

	/**
	 * 配置角色
	 * @param rows
	 * @param userId
	 * @param userName
	 * @return
	 */
	public Result configsRoles(String rows, String userId, String userName);

	/**
	 * 添加新角色
	 * @param roleName
	 * @return
	 */
	public Result addRole(String roleName,String roleDescribe);

	/**
	 * 删除角色
	 * @param roleId
	 * @return
	 */
	public Result deleRole(String roleId);

	/**
	 * 修改密码
	 * @param id
	 * @param pwd
	 * @return
	 */
	public Result modifyPwd(String id, String pwd);

	/**
	 * 注册人脸（刷脸登陆用）
	 * @param faceimg
	 * @param userid
	 * @param username 
	 * @return
	 */
	public Result registerPhoto(MultipartFile faceimg, String userid, String username);
	
}
