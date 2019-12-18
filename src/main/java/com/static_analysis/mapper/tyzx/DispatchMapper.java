package com.static_analysis.mapper.tyzx;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DispatchMapper {

	@Select("<script>"
			+ "select cid,cname,weixindata,dispatchdata from ("
			+ "select cid,cname,string_agg(to_char(weixindata, 'FM999'),',') as weixindata,string_agg(to_char(dispatchdata, 'FM999'),',') as dispatchdata from larrytoolsta "
			+ "where"
			+ " <if test='cid!=\"all\" and (cid!=null and cid!=\"\")'>"
			+ "cid in (${cid}) and "
			+ "</if>"
			+ " time between '${startTime}' and '${endTime}' group by cid) a "
			+ "limit ${end} offset ${start}"
			+ "</script>")
	List<Map<String, Object>> getLarryStaData(@Param("cid")String cid, @Param("startTime")String startTime, @Param("endTime")String endTime, @Param("start")int start, @Param("end")int end);

	@Select("<script>"
			+ "select count(1) from ("
			+ "select cid,cname,string_agg(to_char(weixindata, 'FM999'),',') as weixindata,string_agg(to_char(dispatchdata, 'FM999'),',') as dispatchdata from larrytoolsta "
			+ "where"
			+ " <if test='cid!=\"all\" and (cid!=null and cid!=\"\")'>"
			+ "cid in (${cid}) and "
			+ "</if>"
			+ " time between '${startTime}' and '${endTime}' group by cid) a "
			+ "</script>")
	int getLarryStaDataCounts(@Param("cid")String cid, @Param("startTime")String startTime, @Param("endTime")String endTime);
	
}
