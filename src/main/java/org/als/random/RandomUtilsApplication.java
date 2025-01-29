package org.als.random;

import org.als.random.config.PostgresDatasourceConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
public class RandomUtilsApplication {
	private static final Logger LOGGER = LoggerFactory.getLogger(RandomUtilsApplication.class);

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(RandomUtilsApplication.class, args);
		LOGGER.info(String.format("beans: %s", String.join(System.lineSeparator(), context.getBeanDefinitionNames())));
	}

}
