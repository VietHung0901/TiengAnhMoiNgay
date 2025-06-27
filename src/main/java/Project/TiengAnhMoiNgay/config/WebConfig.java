package Project.TiengAnhMoiNgay.config;

import Project.TiengAnhMoiNgay.model.StringURL;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Ánh xạ URL "/subtitles/**" đến thư mục ngoài
        registry.addResourceHandler("/subtitles/**")
                .addResourceLocations("file:" + StringURL.dirSubtitles);
        registry.addResourceHandler("/writings/**")
                .addResourceLocations("file:" + StringURL.dirFilePath);
        registry.addResourceHandler("/readings/**")
                .addResourceLocations("file:" + StringURL.dirFilePathReading);
    }
}