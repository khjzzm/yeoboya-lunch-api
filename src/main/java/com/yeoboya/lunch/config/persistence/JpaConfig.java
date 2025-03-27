package com.yeoboya.lunch.config.persistence;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "customAuditorProvider")
@DependsOn("flyway")  // Flyway가 먼저 실행되도록 강제
public class JpaConfig {

    @Bean
    public AuditorAware<String> customAuditorProvider() {
        return new AuditorAwareImpl();
    }
}
