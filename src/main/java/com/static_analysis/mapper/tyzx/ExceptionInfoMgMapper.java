package com.static_analysis.mapper.tyzx;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface ExceptionInfoMgMapper {

	/**
	 * 获得异常信息
	 * @param map
	 * @return
	 */
	List<Map<String, Object>> getExceptionInfo(Map<String, Object> map);

	/**
	 * 获得异常信息总数
	 * @param map
	 * @return
	 */
	Integer getExceptionInfoCount(Map<String, Object> map);

	/**
	 * 跟新异常信息
	 * @param id
	 * @param state
	 * @param reasons
	 * @param resolvemethod
	 * @param resolvePersons
	 * @return
	 */
	@Update("update exceptionInfo set state=${state},reasons=#{reasons},resolvemethod=#{resolvemethod},resolvepersons=#{resolvePersons} where id = ${id}")
	Integer doProblem(@Param("id")String id,@Param("state")String state, @Param("reasons")String reasons, @Param("resolvemethod")String resolvemethod, @Param("resolvePersons")String resolvePersons);

	/**
	 * 根据报警类型胡德异常信息
	 * @param map
	 * @return
	 */
	List<Map<String, Object>> getExceptionInfoByType(Map<String, Object> map);

	/**
	 * 获得奔溃信息
	 * @param map
	 * @return
	 */
	List<Map<String, Object>> getMeltModelsInfo(Map<String, Object> map);
}
