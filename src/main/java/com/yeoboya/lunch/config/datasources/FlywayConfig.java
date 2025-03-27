package com.yeoboya.lunch.config.datasources;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

@Profile({"prod"})
@Configuration
@Slf4j
public class FlywayConfig {

    @Autowired
    private DataSource dataSource; // ReplicationDataSourceConfig에서 설정한 DataSource

    @PostConstruct
    public void migrate() {
        log.error("migrate flyway");
        // Flyway 수동 실행
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)  // 데이터 소스 설정
                .locations("classpath:db/migration/h2")  // 마이그레이션 스크립트 경로
                .load();

        flyway.migrate();  // 수동 마이그레이션 실행
    }
}