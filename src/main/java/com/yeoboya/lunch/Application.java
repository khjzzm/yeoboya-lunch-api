package com.yeoboya.lunch;

import com.yeoboya.lunch.config.annotation.ExcludeScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableCaching
//@EnableBatchProcessing
@ComponentScan(
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = {ExcludeScan.class})
        }
)
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
