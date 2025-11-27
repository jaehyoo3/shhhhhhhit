package com.foorend.api.common.config;

import com.foorend.api.common.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * MyBatis 설정 (H2 사용)
 */
@Slf4j
@Configuration
public class MainDBMybatisConfig extends AbstractMybatisConfig {

    @Override
    @Primary
    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory dbSqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();

        configureSqlSessionFactory(sqlSessionFactoryBean, dataSource);

        log.debug("SqlSessionFactory 생성 완료");

        return sqlSessionFactoryBean.getObject();
    }

    @Primary
    @Bean(name = "sqlSession")
    public SqlSession dbSqlSession(SqlSessionFactory sqlSessionFactory) throws GlobalException {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Primary
    @Bean(name = "sqlSessionBatch")
    public SqlSession dbSqlSessionBatch(SqlSessionFactory sqlSessionFactory) throws GlobalException {
        return new SqlSessionTemplate(sqlSessionFactory, ExecutorType.BATCH);
    }
}
