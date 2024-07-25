package org.coder.err.programming._1_code_chapter.serialization.enumusedinapi;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.coder.err.programming.common.Utils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class CommonMistakesApplication {

    public static void main(String[] args) {
        Utils.loadPropertySource(CommonMistakesApplication.class, "jackson.properties");

        System.setProperty("org.springframework.boot.logging.LoggingSystem", "none");
        SpringApplication.run(CommonMistakesApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate(MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter) {
        return new RestTemplateBuilder()
                .additionalMessageConverters(mappingJackson2HttpMessageConverter)
                .build();
    }

    @Bean
    public Module enumModule() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Enum.class, new EnumDeserializer());
        return module;
    }
}

