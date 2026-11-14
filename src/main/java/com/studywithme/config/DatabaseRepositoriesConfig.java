package com.studywithme.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.studywithme.repository",
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = "com\\.studywithme\\.repository\\.mongo\\..*"
        )
)
@EnableMongoRepositories(
        basePackages = "com.studywithme.repository.mongo"
)
public class DatabaseRepositoriesConfig {
}
