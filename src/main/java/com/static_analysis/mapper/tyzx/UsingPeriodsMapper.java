package com.static_analysis.mapper.tyzx;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
public interface UsingPeriodsMapper {

	@Insert("insert into usingperiods(usersums,date,appname) values('${usersums}','${data}',${appcode})")
	Integer  saveUsingPeriodsInfo(@Param("usersums")String usersums,@Param("data")String data,@Param("appcode") Integer appcode);

	String getUsingPeriodsInfo10min(Map<String,Object> map);

	@Select("select usersums from usingperiods where appname=${appName} and date between '${start}' and '${end}'")
	List<String> getUsingPeriodsInfo(@Param("appName")String appName, @Param("start")String start, @Param("end")String end);
	
}
