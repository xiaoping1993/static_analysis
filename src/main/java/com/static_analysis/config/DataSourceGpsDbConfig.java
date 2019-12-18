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
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

/**
 * 添加一个新的数据源（postgresql数据库中的gpsdb数据库配置）
 * @author wangjiping
 *
 */
@Configuration
@MapperScan(basePackages="com.static_analysis.mapper.gpsdb",sqlSessionTemplateRef="gpsdbSqlSessionTemplate")
public class DataSourceGpsDbConfig {
	@Bean(name="gpsdbDataSource")
	@ConfigurationProperties(prefix="spring.datasource.pggpsdb")
	public DataSource gpsdbDataSource(){
		return DataSourceBuilder.create().build();
	}
	@Bean(name="gpsdbSqlSessionFactory")
	public SqlSessionFactory gpsdbSqlSessionFactory(@Qualifier("gpsdbDataSource") DataSource dataSource) throws Exception{
		SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
		bean.setDataSource(dataSource);
		bean.setMapperLocations(new PathMatchingResourcePatternResolver()
		        .getResources("classpath:mappers/gpsdb/*.xml"));
		return bean.getObject();
	}
	@Bean(name = "gpsdbTransactionManager")
	public DataSourceTransactionManager gpsdbTransactionManager(@Qualifier("gpsdbDataSource") DataSource dataSource) {
	    return new DataSourceTransactionManager(dataSource);
	}
	@Bean(name="gpsdbSqlSessionTemplate")
	public SqlSessionTemplate gpsdbSqlSessionTemplate(@Qualifier("gpsdbSqlSessionFactory") SqlSessionFactory sqlSessionFactory)throws Exception{
		return new SqlSessionTemplate(sqlSessionFactory);
	}
}
