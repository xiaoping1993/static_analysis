package com.static_analysis.controller;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.static_analysis.Enum.ResultEnum;
import com.static_analysis.entity.Result;
import com.static_analysis.service.UserMgService;
import com.static_analysis.util.PasswordUtil;
import com.static_analysis.util.ResultUtil;
/**
 * 用户管理
 * @author wangjiping
 *
 */
@RestController
@RequestMapping("/userMg")
public class UserMgController {
	@Autowired
	private UserMgService userMgService;
	protected static final Logger log = LoggerFactory.getLogger(UserMgController.class);
	/**
	 * 获得所有用户信息
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/findAllUser")
	public JSONObject findAllUser(int page,int rows){
		JSONObject allUsers = new JSONObject();
		int start = (page-1)*rows;
		int end = rows;
		int total = userMgService.getAllUserCounts();
		List<Map<String, Object>> list = userMgService.findAllUser(start,end);
		allUsers.put("total", total);
		allUsers.put("rows", list);
		return allUsers;
	}
	/**
	 * 重置密码
	 * @param userId
	 * @param userName
	 * @return
	 */
	@PostMapping("/resetPwd")
	public Result resetPwd(Integer userId,String userName){
		try {
			int count = userMgService.resetPwd(userId,userName);
			if(count!=0){
				return ResultUtil.success();
			}else{
				return ResultUtil.error(ResultEnum.RESET_PASSWORD_FAIL.getCode(), ResultEnum.RESET_PASSWORD_FAIL.getMsg());
			}
		} catch (Exception e) {
			System.out.println(e.toString());
			log.error("更新密码失败");
			return ResultUtil.error(ResultEnum.RESET_PASSWORD_FAIL.getCode(), ResultEnum.RESET_PASSWORD_FAIL.getMsg());
		}
	}
	/**
	 * 删除用户
	 * @param userId
	 * @param userName
	 * @param request
	 * @return
	 */
	@PostMapping("/deleUser")
	public Result deleUser(Integer userId,String userName,HttpServletRequest request){
		try {
			HttpSession session = request.getSession();
			if(session.getAttribute("loginName").toString().equals(userName)){
				return ResultUtil.error(ResultEnum.FORBID_DELE_YOURSERLF.getCode(), ResultEnum.FORBID_DELE_YOURSERLF.getMsg());
			}
			int count = userMgService.deleUser(userId);
			if(count!=0){
				return ResultUtil.success();
			}else{
				return ResultUtil.error(ResultEnum.DELE_USER_FAIL.getCode(), ResultEnum.DELE_USER_FAIL.getMsg());
			}
		} catch (Exception e) {
			System.out.println(e.toString());
			log.error("删除用户失败");
			return ResultUtil.error(ResultEnum.DELE_USER_FAIL.getCode(), ResultEnum.DELE_USER_FAIL.getMsg());
		}
	}
	/**
	 * 获得所有角色信息
	 * @return
	 */
	@RequestMapping("/getRoles")
	public JSONObject getRoles(){
		List<Map<String, Object>> rows = userMgService.getRoles();
		JSONObject list = new JSONObject();
		list.put("total", 0);
		list.put("rows", rows);
		return list;
	}
	/**
	 * 获得用户对应角色id
	 * @param userId
	 * @return
	 */
	@RequestMapping("/getRoleIds")
	public String getRoleIds(Integer userId){
		List<Integer> roleIds = userMgService.getRoleIds(userId);
		String newRoleIds = "";
		if(roleIds.size()!=0){
			newRoleIds+=roleIds.get(0);
		}else{
			return "";
		}
		for(int i=1;i<roleIds.size();i++){
			newRoleIds+=","+roleIds.get(i);
		}
		return newRoleIds;
	}
	/**
	 * 配置用户角色
	 * @param rows
	 * @param userId
	 * @param userName
	 * @return
	 */
	@PostMapping("/configsRoles")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	public Result configsRoles(String rows,String userId,String userName){
		return userMgService.configsRoles(rows,userId,userName);
	}

	/**
	 * 获得所有的权限
	 * @return
	 */
	@PostMapping("/getAuthoritys")
	public Result getAuthoritys(){
		try {
			List<Map<String,Object>> lists = userMgService.getAuthoritys();
			//只获得name和id
			JSONArray authoritys = new JSONArray();
			for (Map<String,Object> list : lists) {
				JSONObject authority = new JSONObject();
				authority.put("id", list.get("id")+","+list.get("pid"));
				authority.put("name", list.get("name"));
				authoritys.add(authority);
			}
			return ResultUtil.success(authoritys);
		} catch (Exception e) {
			log.error(e.toString());
			return ResultUtil.error(ResultEnum.UNKONW_ERROR.getCode(), ResultEnum.UNKONW_ERROR.getMsg());
		}
	}
	/**
	 * 通过角色获得权限
	 * @param roleId
	 * @return
	 */
	@PostMapping("/getAuthoritysByRole")
	public Result getAuthoritysByRole(String roleId){
		try {
			List<Map<String,Object>> lists = userMgService.getAuthoritysByRole(roleId);
			//只获得name和id
			JSONArray authoritys = new JSONArray();
			for (Map<String,Object> list : lists) {
				JSONObject authority = new JSONObject();
				authority.put("id", list.get("id")+","+list.get("pid"));
				authority.put("name", list.get("name"));
				authoritys.add(authority);
			}
			return ResultUtil.success(authoritys);
		} catch (Exception e) {
			log.error(e.toString());
			return ResultUtil.error(ResultEnum.UNKONW_ERROR.getCode(), ResultEnum.UNKONW_ERROR.getMsg());
		}
	}
	/**
	 * 配置角色权限
	 * @param roleId
	 * @param authorityIds
	 * @return
	 */
	@PostMapping("/configRoleAuthoritys")
	public Result configRoleAuthoritys(String roleId,String authorityIds,HttpServletRequest request){
		try {
			String[] list = authorityIds.substring(1, authorityIds.length()-1).split(",");
			if(userMgService.configRoleAuthoritys(roleId,list,request)){
				return ResultUtil.success();
			}else{
				return ResultUtil.error(ResultEnum.UNKONW_ERROR.getCode(), ResultEnum.UNKONW_ERROR.getMsg());
			}
		} catch (Exception e) {
			log.error(e.toString());
			return ResultUtil.error(ResultEnum.UNKONW_ERROR.getCode(), ResultEnum.UNKONW_ERROR.getMsg());
		}
	}
	/**
	 * 添加新角色
	 * @return
	 */
	@RequestMapping("/addRole")
	public Result addRole(String roleName,String roleDescribe){
		try {
			return userMgService.addRole(roleName,roleDescribe);
		} catch (Exception e) {
			log.error(e.toString());
			return ResultUtil.error(ResultEnum.UNKONW_ERROR.getCode(), ResultEnum.UNKONW_ERROR.getMsg());
		}
	}
	/**
	 * 删除角色
	 * @param id
	 * @param roleName
	 * @return
	 */
	@RequestMapping("/deleRole")
	public Result deleRole(String id){
		try {
			return userMgService.deleRole(id);
		} catch (Exception e) {
			log.error(e.toString());
			return ResultUtil.success();
		}
	}
	/**
	 * 有管理员权限的才能调用的接口：修改密码
	 * @param id
	 * @param pwd
	 * @return
	 */
	@RequestMapping("/modifyPwd")
	public Result modifyPwd(String id,String pwd,String name){
		try {
			String newPwd = PasswordUtil.encrypt(pwd, name, PasswordUtil.getStaticSalt());// 加密新密码
			return userMgService.modifyPwd(id,newPwd);
		} catch (Exception e) {
			log.error(e.toString());
			return ResultUtil.error(ResultEnum.UNKONW_ERROR.getCode(),ResultEnum.UNKONW_ERROR.getMsg());
		}
	}
	/**
	 * 刷脸头像注册
	 * @return
	 */
	@RequestMapping("registerPhoto")
	public Result registerPhoto(MultipartFile faceimg,String userid,String username){
		try {
			return userMgService.registerPhoto(faceimg,userid,username);
		} catch (Exception e) {
			log.error(e.toString());
			return ResultUtil.error(ResultEnum.UNKONW_ERROR.getCode(),ResultEnum.UNKONW_ERROR.getMsg());
		}
	}
	
}
