package com.yeoboya.lunch.config.datasources;

import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;

@Configuration
public class FlywayConfig {

    @Autowired
    private DataSource dataSource; // ReplicationDataSourceConfig에서 설정한 DataSource

    @PostConstruct
    public void migrate() {
        // Flyway 수동 실행
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)  // 데이터 소스 설정
                .locations("classpath:db/migration/mysql")  // 마이그레이션 스크립트 경로
                .load();

        flyway.migrate();  // 수동 마이그레이션 실행
    }
}