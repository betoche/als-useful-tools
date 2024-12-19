package org.als.random.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;

@Configuration
public class PostgresDatasourceConfiguration {

    @Bean
    @ConfigurationProperties("spring.datasource.random")
    public DataSourceProperties randomDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    public DataSource randomDataSource() {
        return randomDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    /*
    @Bean
    public JdbcTemplate todosJdbcTemplate(@Qualifier("randomDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
    */

    @Primary
    @Bean(name="randomEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean
            (EntityManagerFactoryBuilder builder, @Qualifier("randomDataSource") DataSource dataSource){

        return builder.dataSource(dataSource)
                .packages("org.als.random.entity")
                .persistenceUnit("random").build();
    }
}
