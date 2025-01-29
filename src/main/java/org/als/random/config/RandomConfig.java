package org.als.random.config;

import org.als.random.domain.RandomConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;

@Configuration
public class RandomConfig {

    @Bean
    public RandomConfiguration randomConfiguration() {
        Yaml yaml = new Yaml(new Constructor(RandomConfiguration.class, new LoaderOptions()));
        InputStream inputStream = RandomConfiguration.class.getClassLoader().getResourceAsStream("home.yml");
        return yaml.load(inputStream);
    }
}
