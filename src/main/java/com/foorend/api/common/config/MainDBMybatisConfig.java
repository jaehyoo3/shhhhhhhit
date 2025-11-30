package com.foorend.api.common.config;

import com.foorend.api.common.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * MainDB MyBatis 설정
 */
@Slf4j
@Configuration
public class MainDBMybatisConfig extends AbstractMybatisConfig {

    @Override
    @Primary
    @Bean(name = "mainDBSqlSessionFactory")
    public SqlSessionFactory dbSqlSessionFactory(@Qualifier("mainDBDataSource") DataSource objDataSource) throws Exception {
        SqlSessionFactoryBean objSqlSessionFactoryBean = new SqlSessionFactoryBean();

        configureSqlSessionFactory(objSqlSessionFactoryBean, objDataSource);

        log.debug("MainDB SqlSessionFactory 생성 완료");

        return objSqlSessionFactoryBean.getObject();
    }

    @Primary
    @Bean(name = "mainDBSqlSession")
    public SqlSession dbSqlSession(@Qualifier("mainDBSqlSessionFactory") SqlSessionFactory objSqlSessionFactory) throws GlobalException {
        return new SqlSessionTemplate(objSqlSessionFactory);
    }

    @Primary
    @Bean(name = "mainDBSqlSessionBatch")
    public SqlSession dbSqlSessionBatch(@Qualifier("mainDBSqlSessionFactory") SqlSessionFactory objSqlSessionFactory) throws GlobalException {
        return new SqlSessionTemplate(objSqlSessionFactory, ExecutorType.BATCH);
    }
}
