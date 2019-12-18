package com.static_analysis.mapper.tyzx;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface UserMgMapper {
	@Select("select a.name as userName,a.id as userId,string_agg(b. NAME,',') AS roleName,string_agg(to_char(A .role_id,'FM9999'),',') AS roleId,a.hasfaceregister from (select name,u.id,role_id,hasfaceregister from \"user\" u left join user_role ur on u.id=ur.user_id) a left join role b on a.role_id=b.id group by username,userid,hasfaceregister limit ${end} offset ${start}")
	public List<Map<String,Object>> findAllUser(@Param("start")int start, @Param("end")int end);

	@Select("select count(1) from (select name,u.id,role_id from \"user\" u left join user_role ur on u.id=ur.user_id) a join role b on a.role_id=b.id")
	public int getAllUserCounts();

	@Update("update \"user\" set password=#{defaultPwd} where id = ${userId}")
	public Integer resetPwd(@Param("userId")Integer userId,@Param("defaultPwd") String defaultPwd);

	@Update("delete from \"user\" where id = ${userId}")
	public Integer deleUser(@Param("userId")Integer userId);

	@Select("select id,name from role")
	public List<Map<String, Object>> getRoles();

	@Select("select role_id from user_role where user_id=${userId}")
	public List<Integer> getRoleIds(@Param("userId")Integer userId);

	@Insert("insert into user_role(user_id,role_id) values(${userId},${id})")
	public Integer insertUserRole(@Param("id")Integer id, @Param("userId")Integer userId);

	@Delete("delete from user_role where user_id=#{userId}")
	public Integer deleUserRole(@Param("userId")Integer userId);

	@Select("select id from \"user\" where name = #{userName}")
	public Integer getUserIdByUserName(@Param("userName")String userName);

	@Select("select u.id,u.name from \"user\" u where id not in(select DISTINCT(user_id) from user_role) ")
	public List<Map<String, Object>> getAllUserNoRole();

	@Select("select count(1) from \"user\" where name = #{userName}")
	public Integer existUser(@Param("userName")String userName);

	@Insert("insert into \"user\"(name,password) values(#{userName},#{pwd})")
	public Integer addUser(@Param("userName")String userName, @Param("pwd")String pwd);

	@Select("select id,pid,name,url from menus order by \"order\"")
	public List<Map<String,Object>> getAuthoritys();

	@Select("select m.id,m.pid,m.name,m.url from menus m left join role_menus rm on m.id=rm.menus_id where rm.role_id=${roleId}")
	public List<Map<String,Object>> getAuthoritysByRole(@Param("roleId")String roleId);

	@Delete("delete from role_menus where role_id=${roleId}")
	public Integer deleRoleAllAuthoritys(@Param("roleId")String roleId);

	@Insert("insert into role_menus(role_id,menus_id) values(${roleId},${authorityId})")
	public Integer insertRoleAuthoritys(@Param("roleId")String roleId, @Param("authorityId")String authorityId);

	@Insert("insert into role(name,describe) values(#{roleName},#{describle})")
	public Integer addRole(@Param("roleName")String roleName,@Param("describle")String roleDescribe);

	@Delete("delete from role where id = ${roleId}")
	public Integer deleRole(@Param("roleId")String roleId);

	@Update("update \"user\" set password='${pwd}' where id = ${id}")
	public Integer modifyPwd(@Param("id")String id,@Param("pwd")String pwd);

	@Update("update \"user\" set hasfaceregister=${i} where id = ${userid}")
	public Integer updateUserForhasfaceregister(@Param("userid")String userid,@Param("i")int i);
}
