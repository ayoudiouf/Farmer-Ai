package sn.farmerai.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.storage.local-path:./storage/photos}")
    private String basePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String cheminAbsolu = "file:" + basePath.replace("./", "") + "/";
        registry.addResourceHandler("/photos/**")
                .addResourceLocations(cheminAbsolu);
    }
}