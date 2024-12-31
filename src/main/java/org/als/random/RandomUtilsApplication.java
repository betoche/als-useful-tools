package org.als.random;

import org.als.random.config.PostgresDatasourceConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(basePackages = {"org.als.random.config", "org.als.random.entity", "org.als.random.repo",
		"org.als.random.controller", "org.als.random.domain", "org.als.teamconnect.config", "org.als.teamconnect.entity",
		"org.als.teamconnect.repo", "org.als.teamconnect.controller"},
		excludeFilters = {
		@Filter(type = FilterType.ASSIGNABLE_TYPE, value = PostgresDatasourceConfiguration.class)
})
public class RandomUtilsApplication {

	public static void main(String[] args) {
		//SpringApplication.run(RandomUtilsApplication.class, args);
		ConfigurableApplicationContext context = SpringApplication.run(RandomUtilsApplication.class, args);
		//GreetingClient greetingClient = context.getBean(GreetingClient.class);
		// We need to block for the content here or the JVM might exit before the message is logged
		//System.out.println(">> message = " + greetingClient.getMessage().block());
	}

}
