package com.foorend.api.common.config;

import com.foorend.api.common.exception.GlobalException;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.io.IOException;

public abstract class AbstractMybatisConfig {
	@Value("${mybatis.config-location}")
	private String mybatisConfigLocation;

	@Value("${mybatis.mapper-location}")
	private String mybatisMapperLocation;

	public abstract SqlSessionFactory dbSqlSessionFactory(DataSource objDataSource) throws Exception;
	public abstract SqlSession dbSqlSession(SqlSessionFactory objSqlSessionFactory) throws GlobalException;


	protected void configureSqlSessionFactory(SqlSessionFactoryBean objSqlSessionFactoryBean, DataSource objDataSource) throws IOException {
		PathMatchingResourcePatternResolver objPathResolver = new PathMatchingResourcePatternResolver();
		objSqlSessionFactoryBean.setDataSource(objDataSource);
		objSqlSessionFactoryBean.setConfigLocation(objPathResolver.getResource(mybatisConfigLocation));
		objSqlSessionFactoryBean.setMapperLocations(objPathResolver.getResources(mybatisMapperLocation));
		objSqlSessionFactoryBean.setTypeAliasesPackage("com.foorend.api");
		objSqlSessionFactoryBean.setVfs(SpringBootVFS.class);
	}
}