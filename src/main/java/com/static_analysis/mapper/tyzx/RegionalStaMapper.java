package com.static_analysis.mapper.tyzx;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface RegionalStaMapper {

	@Select("select id, name from ty_conf_division")
	List<Map<String,Object>> getAllDivisonNames();

	@Insert("insert into ty_conf_division(code,center,level,name) values(#{adcode},#{center},#{level},#{name})")
	Integer insertDivistion(@Param("name")String name, @Param("level")String level, @Param("adcode")String adcode, @Param("center")String center);

	@Select("<script>"
			+ "select a.code,coalesce(b.count,0) as count from (select code from ty_conf_division where level='1') a LEFT JOIN (select count(1) as count,province_adcode from ty_rj_situation "
			+ " where "
			+ " <if test='appName!=\"all\" and (appName!=null and appName!=\"\")'>"
			+ "	appName = ${appName} and "
			+ "	</if>"
			+ " <if test='operateSystem!=\"all\" and (operateSystem!=null and operateSystem!=\"\")'>"
			+ "	operateSystem = ${operateSystem} and "
			+ "</if>"
			+ " <choose>"
			+ "		<when test='accounts==\"all\"'>"
			+ "			<if test='cids!=\"\"'>"
			+ "				cid in (${cids}) and"
			+ "			</if>"
			+ "		</when>"
			+ "		<otherwise>"
			+ "			login_name = #{accounts} and"
			+ "		</otherwise>"
			+ " </choose>"
			+ " 1=1"
			+ " GROUP BY province_adcode) b on a.code=b.province_adcode"
			+ "</script>")
	List<Map<String, Object>> getRegionsalAdcodeXiajiShengValueJSON(@Param("appName")String appName, @Param("operateSystem")String operateSystem,@Param("cids")String cids, @Param("accounts")String accounts);

	@Select("<script>"
			+ "select a.code,coalesce(b.count,0) as count  from (select code from ty_conf_division where level='2' and code like #{myAdcode}) a LEFT JOIN (select city_adcode,count(1) as count from ty_rj_situation "
			+ " where"
			+ " province_adcode=#{adcode} and "
			+ " <if test='appName!=\"all\" and (appName!=null and appName!=\"\")'>"
			+ "	appName = ${appName} and "
			+ "</if>"
			+ " <if test='operateSystem!=\"all\" and (operateSystem!=null and operateSystem!=\"\")'>"
			+ "	operateSystem = ${operateSystem} and "
			+ "</if>"
			+ " <choose>"
			+ "		<when test='accounts==\"all\"'>"
			+ "			<if test='cids!=\"\"'>"
			+ "			  cid in (${cids}) and"
			+ "			</if>"
			+ "		</when>"
			+ "		<otherwise>"
			+ "			login_name in (#{accounts}) and"
			+ "		</otherwise>"
			+ " </choose>"
			+ "1=1"
			+ " GROUP BY city_adcode) b on a.code=b.city_adcode"
			+ "</script>")
	List<Map<String, Object>> getRegionsalXiajiShiAdcodeValueJSON(@Param("adcode")String adcode,@Param("myAdcode")String myAdcode, @Param("appName")String appName, @Param("operateSystem")String operateSystem, @Param("cids")String cids, @Param("accounts")String accounts);

	@Select("<script>"
			+ "select a.code,coalesce(b.count,0) as count  from (select code from ty_conf_division where level='3' and code like #{myAdcode}) a LEFT JOIN (select district_adcode,count(1) as count from ty_rj_situation "
			+ "where"
			+ " province_adcode=#{adcode} and" 
			+ " <if test='appName!=\"all\" and (appName!=null and appName!=\"\")'>"
			+ "	appName = ${appName} and "
			+ "</if>"
			+ " <if test='operateSystem!=\"all\" and (operateSystem!=null and operateSystem!=\"\")'>"
			+ "	operateSystem = ${operateSystem} and "
			+ "</if>"
			+ " <choose>"
			+ "		<when test='accounts==\"all\"'>"
			+ "			<if test='cids!=\"\"'>"
			+ "				cid in (${cids}) and"
			+ "			</if>"
			+ "		</when>"
			+ "		<otherwise>"
			+ "			login_name in (#{accounts}) and"
			+ "		</otherwise>"
			+ " </choose>"
			+ "1=1"
			+ " GROUP BY district_adcode) b on a.code=b.district_adcode"
			+ "</script>")
	List<Map<String, Object>> getRegionsalXiajiQuAdcodeValueJSON(@Param("adcode")String adcode, @Param("myAdcode")String myAdcode, @Param("appName")String appName, @Param("operateSystem")String operateSystem, @Param("cids")String cids, @Param("accounts")String accounts);
}
