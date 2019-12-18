package com.static_analysis.mapper.tyzx;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface LoginMapper {
	
	@Select("select count(1) from \"user\" where name = #{userName}")
	public Integer checkUser(@Param("userName")String username);
	
	@Select(
					"select a.name,a.role_id,a.menus_id,m.pid as menus_pid from "+
					"(SELECT a.name,ra.role_id,ra.menus_id "+
					"FROM "+
					"	(SELECT u. NAME,role_id "+
					"		FROM \"user\" u LEFT JOIN user_role ur ON u.id = ur.user_id "+
					"		  WHERE u. NAME = #{name} AND u. PASSWORD = #{password} "+
					"	) a "+
					"LEFT JOIN role_menus ra ON a.role_id = ra.role_id) a "
					+ "left join menus m on a.menus_id=m.id")
	public List<Map<String,Object>> getLogin(@Param("name")String name, @Param("password")String password);
	
	@Select("select password from \"user\" where name =#{name} limit 1 offset 0")
	public String getUserPwByName(@Param("name") String loginName);
	@Update("update \"user\" set password = #{password} where name = #{name}")
	public Integer savePw(@Param("name") String loginName,@Param("password") String password);

	@Select("select * from \"user\" where name = #{loginName}")
	public Map<String,Object> getUserInfoByName(@Param("loginName")String loginName);

	@Select("select DISTINCT(version) from ty_rj_situation order by version")
	public List<String> getAppVersions();
	@Select("SELECT value FROM \"accessToken\" where name = #{name} limit 1")
	public String getAccessToken(@Param("name") String name);
	@Update("update \"accessToken\" set value=#{value} where name=#{name}")
	public int setAccessToken(@Param("value") String value,@Param("name") String name);
}
