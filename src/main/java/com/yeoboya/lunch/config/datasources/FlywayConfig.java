package com.yeoboya.lunch.config.datasources;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.MigrationInfoService;
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
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration/mysql")
                .sqlMigrationPrefix("V")
                .sqlMigrationSeparator("__")
                .sqlMigrationSuffixes(".sql")
                .baselineOnMigrate(true)
                .baselineVersion("1")
                .load();

        flyway.baseline();          // baseline-on-migrate 설정과 충돌하지 않게 직접 호출 (안정적)
        printFlywayInfo(flyway);
        flyway.migrate();           // 실제 마이그레이션 실행
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