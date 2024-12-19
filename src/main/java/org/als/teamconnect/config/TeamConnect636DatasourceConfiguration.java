package org.als.teamconnect.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        entityManagerFactoryRef = "teamconnect636EntityManagerFactory",
        transactionManagerRef = "teamconnect636TransactionManager",
        basePackages = {"org.als.teamconnect.entity", "org.als.teamconnect.repo"}
)
public class TeamConnect636DatasourceConfiguration {

    @Bean
    @ConfigurationProperties("spring.datasource.teamconnect636")
    public DataSourceProperties teamconnect636Properties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource teamconnect636Datasource() {
        return teamconnect636Properties()
                .initializeDataSourceBuilder()
                .build();
    }

    /*
    @Bean
    public JdbcTemplate topicsJdbcTemplate(@Qualifier("teamconnect636DataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
    */

    @Bean
    public PlatformTransactionManager teamconnect636TransactionManager(
            @Qualifier("teamconnect636EntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);

    }

    @Bean(name="teamconnect636EntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean teamconnect636EntityManagerFactory
            (EntityManagerFactoryBuilder builder, @Qualifier("teamconnect636Datasource") DataSource dataSource){

        return builder.dataSource(dataSource)
                .packages("org.als.teamconnect.entity")
                .persistenceUnit("teamconnect636").build();
    }
}
