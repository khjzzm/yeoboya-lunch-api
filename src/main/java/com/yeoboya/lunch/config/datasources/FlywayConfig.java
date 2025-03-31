package com.yeoboya.lunch.config.datasources;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.MigrationInfoService;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

@Profile({"dev", "prod"})
@Configuration
@Slf4j
public class FlywayConfig {

    private final DataSource dataSource; // ReplicationDataSourceConfig에서 설정한 DataSource

    public FlywayConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void migrate() {
        // Flyway 객체를 구성하고 설정을 적용
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)  // 사용할 데이터소스 설정 (DB 연결)
                .locations("classpath:db/migration/mysql")  // 마이그레이션 파일 경로 설정
                .sqlMigrationPrefix("V")  // 마이그레이션 파일 이름 접두사 (예: V1__init.sql)
                .sqlMigrationSeparator("__")  // 접두사와 설명 사이 구분자 (예: V1__init)
                .sqlMigrationSuffixes(".sql")  // 마이그레이션 파일 확장자
                .baselineOnMigrate(true)  // flyway_schema_history가 없을 경우 자동으로 baseline 수행
                .baselineVersion("1")  // baseline의 기준 버전 지정 (보통 V1부터 시작)
                .load();  // Flyway 인스턴스 생성

        // baselineOnMigrate와 중복 방지를 위해 명시적으로 baseline 수행 (더 안전하고 명확한 방식)
        flyway.baseline();

        // 현재 Flyway 마이그레이션 상태 출력 (버전, 상태 등 로그로 확인용)
        printFlywayInfo(flyway);

        // 마이그레이션 실행 (V2부터 실행됨. V1은 baseline 처리됨)
        flyway.migrate();
    }


    private void printFlywayInfo(Flyway flyway) {
        MigrationInfoService info = flyway.info();

        log.info("=== Flyway Migration Info ===");

        for (MigrationInfo migration : info.all()) {
            log.info("Version: {}, Description: {}, State: {}",
                    migration.getVersion(),
                    migration.getDescription(),
                    migration.getState());
        }

        log.info("==============================");
    }
}