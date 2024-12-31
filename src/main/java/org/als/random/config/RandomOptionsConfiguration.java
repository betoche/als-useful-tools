package org.als.random.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import java.util.List;

/*
@Configuration
@EnableConfigurationProperties(RandomOptionsConfiguration.MyRandomOptions.class)
*/
@PropertySource(value = "classpath:/home.yml")
@ConfigurationProperties
@Component
@Data
public class RandomOptionsConfiguration {
    //@Value("${name}")
    private String name;
    //@Value("${string-list}")
    private List<String> stringList;
    //@Value("${genericOptions}")
    private List<Option> genericOptions;
    //@Value("${specificOptions}")
    private List<Option> specificOptions;

    @Data
    public static class Option {
        private String title;
        private String description;
        private String path;
    }
}
