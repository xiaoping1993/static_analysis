package com.static_analysis.mapper.tyzx;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface MainMapper {

	@Select("select id,pid,name,url,iconname from menus where id in (${menus_ids}) order by \"order\"")
	List<Map<String, Object>> getMenusInfo(@Param("menus_ids")String menus_ids);

	@Insert("insert into ty_rj_situation(mphone_imei,mphone_brand,mphone_mode,version,province_adcode,city_adcode,district_adcode,cid,cname,login_name,appName,operateSystem,create_time,update_time) values(#{mphone_imei},#{mphone_brand},#{mphone_mode},#{version},#{province_adcode},#{city_adcode},#{district_adcode},#{cid},#{cidName},#{loginName},${appName},${operateSystem},#{createTime},#{updateTime})")
	Integer insertLoginInfo(@Param("mphone_imei")String mphone_imei, @Param("mphone_brand")String mphone_brand,@Param("mphone_mode")String mphone_mode, @Param("version") String version, @Param("province_adcode")String province_adcode,
			@Param("city_adcode")String city_adcode, @Param("district_adcode")String district_adcode,@Param("cid")String cid, @Param("cidName")String cidName, @Param("loginName")String loginName,  @Param("appName")Integer appName,  @Param("operateSystem")Integer operateSystem,@Param("createTime") Timestamp createTime, @Param("updateTime")Timestamp updateTime);

	@Select("select code from ty_conf_division where level = '3' and code is not null")
	List<String> getDistrict_adcode();

	@Select("select id, mphone_brand,mphone_mode,version,province_adcode,city_adcode,district_adcode,login_name,appName,operateSystem from ty_rj_situation where mphone_imei=#{mphone_imei}")
	Map<String,Object> hasMphoneInfoByImei(@Param("mphone_imei")String mphone_imei);

	@Select("update ty_rj_situation set mphone_brand = #{mphone_brand}, mphone_mode = #{mphone_mode},version=#{version},province_adcode=#{province_adcode},city_adcode=#{city_adcode},district_adcode=#{district_adcode},cid=#{cid},cname=#{cidName},login_name=#{loginName},appName=#{appName},operateSystem=#{operateSystem},update_time=#{time} where id = #{id}")
	void updateLoginInfo(@Param("mphone_brand")String mphone_brand,@Param("mphone_mode")String mphone_mode, @Param("version")String version, @Param("province_adcode")String province_adcode,
			@Param("city_adcode")String city_adcode,@Param("district_adcode") String district_adcode, @Param("cid")String cid, @Param("cidName")String cidName,@Param("loginName") String loginName, @Param("appName")Integer appName, @Param("operateSystem")Integer operateSystem,
			@Param("time")Timestamp time, @Param("id")Integer id);

	@Insert("insert into pushStatistics(appName,pushTotals,clickTotals,time) values(${appName},${pushTotals},${clickTotals},'${time}')")
	Integer savePushInfo(@Param("appName")String appName, @Param("pushTotals")String pushTotals, @Param("clickTotals")String clickTotals,@Param("time")String time);

	@Select("select code from warnType")
	List<String> getWarnTypes();

	@Insert("insert into warnclickInfo(appName,warnType,clickSum,time) values(${appName},#{warnType},${value},'${data}')")
	Integer saveWarnClickInfo(@Param("appName")String appName,@Param("warnType")String warnType, @Param("value")String value, @Param("data")String data);

	@Insert("insert into page_park_time_detail (loginName,customerid,customername,mphone_imei,appName,pageCode,time) values(#{loginName},${cid},#{cidName},#{mphone_imei},${appName},${pageCode},'${time}')")
	Integer savePageInfo(@Param("loginName")String loginName,@Param("cid")String cid,@Param("cidName")String cidName,@Param("mphone_imei")String mphone_imei, @Param("appName")String appName, @Param("pageCode")String pageCode, @Param("time")String time);

	@Select("select loginName,mphone_imei,appName,pageCode,time,customerid as cid,customername as cidname from page_park_time_detail a where to_char(a.time,'YYYY-MM-DD') = '${yesterday}' ORDER BY loginName,mphone_imei,appName,time")
	List<Map<String, Object>> getParkingTimeDetail(@Param("yesterday")String yesterday);

	@Insert("Insert into page_park_time(pagecode,parkingtime,date,mphone_imei,customerid,customername,login_name) values(${pageCode},${value},'${yesterday}',#{mphone_imei},${customerId},#{customerName},#{loginName})")
	Integer saveParkingtime(@Param("pageCode")String pageCode, @Param("value")Long value, @Param("yesterday")String yesterday, @Param("mphone_imei")String mphone_imei, @Param("loginName")String loginName,
			@Param("customerId")String cid, @Param("customerName")String cidName);

	@Insert("Insert into exceptionInfo(appName,mphone_imei,type,exception_msg,exception_detailmsgString,exception_detailmsgfile,date) values(${appName},#{mphone_imei},${type},#{exception_msg},#{exception_detailmsgString},#{fileName},'${date}')")
	Integer saveExceptionInfo( @Param("appName")String appName,@Param("mphone_imei")String mphone_imei,@Param("type")String type ,@Param("exception_msg")String exception_msg, @Param("exception_detailmsgString")String exception_detailmsgString,
			@Param("fileName")String fileName, @Param("date")String date);

	@Select("select * from pushstatistics where time='${time}%' and appname = ${appCode} limit 1 offset 0")
	Map<String,Object> getPushstatistics(@Param("appCode")String appCode, @Param("time")String time);

	@Update("update pushstatistics set clicktotals=${totals} where time='${reciptTime}%' and appname=${appCode}")
	Integer updataPushstatistics(@Param("totals")Integer totals, @Param("appCode")String appCode, @Param("reciptTime")String reciptTime);

	@Select("select * from warnclickinfo where appname=${appCode} and warntype=#{warnType} and time = '${reciptTime}%' limit 1 offset 0")
	Map<String, Object> getWarnClickInfo(@Param("appCode")String appCode, @Param("warnType")String warnType, @Param("reciptTime")String reciptTime);

	@Update("update warnclickinfo set clicksum=${clickSum1} where id=${id}")
	Integer updataWarnClickInfo(@Param("clickSum1")Integer clickSum1, @Param("id")String id);

}
