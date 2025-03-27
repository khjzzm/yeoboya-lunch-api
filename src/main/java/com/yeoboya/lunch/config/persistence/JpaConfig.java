package com.yeoboya.lunch.config.persistence;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "customAuditorProvider")
public class JpaConfig {

    @Bean
    public AuditorAware<String> customAuditorProvider() {
        return new AuditorAwareImpl();
    }
}
