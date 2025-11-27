package com.foorend.api.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * H2 DataSource 설정 (개발/테스트용)
 * - DataSource는 Spring Boot 자동 설정 사용
 * - schema.sql 초기화만 별도 설정
 */
@Configuration
@EnableTransactionManagement
public class DataSourceConfig {

    @Value("classpath:schema.sql")
    private Resource schemaScript;

    /**
     * H2 DB 초기화 - 서버 시작 시 schema.sql 자동 실행
     */
    @Bean
    public DataSourceInitializer dataSourceInitializer(DataSource dataSource) {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(schemaScript);
        populator.setSqlScriptEncoding("UTF-8");
        populator.setContinueOnError(true);

        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        initializer.setDatabasePopulator(populator);
        return initializer;
    }
}
