package com.static_analysis.mapper.gpsdb;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
public interface FunctionUsedInfoGpsdbMapper {

	/**
	 * 获得客户树信息
	 * @param targetCid
	 * @param targetOid
	 * @param customerPidSql
	 * @return
	 */
	@Select("select a.id,a.name,a.childs,string_agg(login_name,',') as accounts from (select a.id,a.name,a.childs,b.login_name  from  (SELECT a.id,a.name,COUNT(b.id) AS childs FROM ty_customer a LEFT JOIN ty_customer b ON a.id = b.pid WHERE ${customerPidSql} GROUP BY a.id order by a.name asc) a left join ty_operator b on a.id=b.customer_id) a group by id,name,childs")
	List<Map<String,Object>> cusTree4All(@Param("targetCid")Integer targetCid,@Param("targetOid") Integer targetOid,@Param("customerPidSql") String customerPidSql);
	/**
	 * 根据cid获得客户信息
	 * @param cid
	 * @return
	 */
	@Select("select * from ty_customer where id = ${cid}")
	Map<String, Object> getCustomerById(@Param("cid")String cid);

	/**
	 * 
	 * @param path
	 * @param id
	 * @param search
	 * @return
	 */
	@Select("SELECT c.id AS id,c. name AS name,'customer' AS type,REPLACE (c.path,'${path}','#${id}#') AS path "
			+ "FROM ty_customer c WHERE "
			+ "c.path LIKE '${path}%' AND c. NAME LIKE '%${search}%' "
			+ "ORDER BY c. NAME  ASC LIMIT 20")
	List<Map<String, Object>> getCustomerList(@Param("path")String path, @Param("id")String id, @Param("search")String search);

	@Select("SELECT c.id FROM ty_customer c JOIN ty_customer c2 ON c.path LIKE CONCAT(c2.path, '%') WHERE c2.id IN ${cids}")
	List<String> getCpidsByids(@Param("cids")String cids);

	@Select("select name from ty_customer where id in ${cidsString}")
	List<String> getCidNamesBycids(@Param("cidsString")String cidsString);
	
}
