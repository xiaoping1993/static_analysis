package com.static_analysis.mapper.tyzx;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface InfoMgMapper {

	@Select("select \"order\",id,pid,name,url from menus order by \"order\" limit ${end} offset ${start}")
	List<Map<String, Object>> getAllMenus(@Param("start")int start, @Param("end")int end);

	@Select("select count(1) from menus")
	Integer getAllMenusCount();

	@Update("update menus set pid = #{pid},name=#{name},url=#{url},\"order\"=${order} where id = #{id}")
	Integer menusUpdate(@Param("id")Integer id,@Param("pid") Integer pid, @Param("name")String name, @Param("url")String url, @Param("order")Integer order);

	@Insert("insert into menus(pid,name,url,\"order\") values(#{pid},#{name},#{url},${order})")
	Integer menusInsert(@Param("pid")Integer pid, @Param("name")String name, @Param("url")String url, @Param("order")Integer order);

	@Delete("delete from menus where id = #{id}")
	Integer menusDele(@Param("id")Integer id);

	@Select("select a.\"order\",a.id, a.name,count(b.id) as childs from menus a LEFT JOIN menus b on a.id=b.pid where a.pid=${pid} GROUP BY a.id ORDER BY a.id")
	List<Map<String, Object>> getAllmenusJSONTree(@Param("pid")String pid);

	@Select("select string_agg(to_char(menus_id, 'FM999'),',')  from role_menus where role_id in (select b.role_id from \"user\" a LEFT JOIN user_role b on a.id=b.user_id where a.name=#{loginName})")
	String getMenus_idsByLoginName(@Param("loginName")String loginName);

	@Select("select * from menus where id = ${id}")
	Map<String,Object> getMenusById(@Param("id")Integer id);
}
