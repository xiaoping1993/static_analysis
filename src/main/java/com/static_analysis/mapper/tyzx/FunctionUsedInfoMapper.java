package com.static_analysis.mapper.tyzx;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
public interface FunctionUsedInfoMapper {

	/**
	 * 获得推送点击率信息
	 * @param map
	 * @return
	 */
	List<Map<String, Object>> getPushClickRateInfoData(Map<String, Object> map);
	/**
	 * 获得推送点击率总数
	 * @param map
	 * @return
	 */
	Integer getPushClickRateInfoTotal(Map<String, Object> map);

	@Select("select * from c.id AS id,c. NAME AS NAME,'customer' AS type,REPLACE (c.path,'${path}','#${id}#') as path "
			+ "from ty_customer c "
			+ "where  c.path like '${path}' and c.name like '%${search}%' "
			+ "group by "
			+ "CONVERT (c. NAME USING gbk) ASC"
			+ "LIMIT 20")
	List<Map<String, Object>> getCustomerList(String path, String id, String search);

	/**
	 * 获得报警详细信息
	 * @param appName
	 * @param data
	 * @return
	 */
	@Select("select b.name as name,a.clicksum as sum from warnclickInfo a left JOIN warnType b on a.warnType=b.code where a.appName=${appName} and a.time='${data}'")
	List<Map<String, Object>> getwarnDetails(@Param("appName")String appName, @Param("data")String data);

	/**
	 * 获得页面停留时长信息
	 * @param cid
	 * @param timing
	 * @return
	 */
	@Select("select name as pagename,coalesce(sum(parkingtime),0) as parkingtime from warnType b LEFT JOIN page_park_time a on a.pageCode=b.code where customerId =${cid} and  date >=date_add(curdate(),INTERVAL -${timing} day) GROUP BY name")
	List<Map<String, Object>> getPageParktimeInfoData(@Param("cid")String cid, @Param("timing")String timing);

	/**
	 * 获得对应app关注的页面数据
	 * @param appName
	 * @return
	 */
	List<Map<String, Object>> getfocusPages(@Param("appName")String appName);

	/**
	 * 获得页面停留时长
	 * @param map
	 * @return
	 */
	List<Integer> getPageParkingtimes(Map<String,Object> map);

}
