package com.foorend.api.common.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * DataSource 및 트랜잭션 설정
 */
@Configuration
@EnableTransactionManagement
public class DataSourceConfig {

    @Bean
    @ConfigurationProperties(prefix = "maindb")
    public HikariConfig mainDBHikariConfig() {
        return new HikariConfig();
    }

    @Primary
    @Bean(name = "mainDBDataSource")
    public DataSource mainDBDataSource() {
        return new HikariDataSource(mainDBHikariConfig());
    }

    /**
     * 트랜잭션 매니저
     */
    @Primary
    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager(@Qualifier("mainDBDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
