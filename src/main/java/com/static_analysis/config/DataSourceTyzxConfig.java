package com.static_analysis.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

/**
 * 添加一个新的数据源（项目主数据库masdb）
 * @author wangjiping
 *
 */
@Configuration
@MapperScan(basePackages="com.static_analysis.mapper.tyzx",sqlSessionTemplateRef="tyzxSqlSessionTemplate")
public class DataSourceTyzxConfig {
	@Bean(name="tyzxDataSource")
	@ConfigurationProperties(prefix="spring.datasource.tyzx")
	@Primary
	public DataSource tyzxDataSource(){
		return DataSourceBuilder.create().build();
	}
	@Bean(name="tyzxSqlSessionFactory")
	@Primary
	public SqlSessionFactory tyzxSqlSessionFactory(@Qualifier("tyzxDataSource") DataSource dataSource) throws Exception{
		SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
		bean.setDataSource(dataSource);
		bean.setMapperLocations(new PathMatchingResourcePatternResolver()
		        .getResources("classpath:mappers/tyzx/*.xml"));
		return bean.getObject();
	}
	@Bean(name = "tyzxTransactionManager")
	@Primary
	public DataSourceTransactionManager tyzxTransactionManager(@Qualifier("tyzxDataSource") DataSource dataSource) {
	    return new DataSourceTransactionManager(dataSource);
	}
	@Bean(name="tyzxSqlSessionTemplate")
	public SqlSessionTemplate tyzxSqlSessionTemplate(@Qualifier("tyzxSqlSessionFactory") SqlSessionFactory sqlSessionFactory)throws Exception{
		return new SqlSessionTemplate(sqlSessionFactory);
	}
}
