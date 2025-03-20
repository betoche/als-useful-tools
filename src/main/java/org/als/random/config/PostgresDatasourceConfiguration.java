package org.als.random.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/*
@Configuration
@EnableJpaRepositories(
        entityManagerFactoryRef = "randomEntityManagerFactory",
        transactionManagerRef = "randomTransactionManager",
        basePackages = "org.als.random.repository"
)
@EntityScan("org.als.random.entity")
*/
public class PostgresDatasourceConfiguration {

    @Bean
    @ConfigurationProperties("spring.datasource.random")
    public DataSourceProperties randomDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(name="randomEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean
            (EntityManagerFactoryBuilder builder, @Qualifier("randomDataSource") DataSource dataSource) {
        return builder.dataSource(dataSource)
                .packages("org.als.random.entity", "org.als.random.repository")
                .persistenceUnit("random").build();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.random")
    public DataSource randomDataSource() {
        return randomDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    @Bean
    public PlatformTransactionManager randomTransactionManager(
            @Qualifier("randomEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);

    }


}
