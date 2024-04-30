package vizicard.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CORSConfig implements WebMvcConfigurer {

    @Value("${front-url-base}")
    private String urlBase;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String base = urlBase.substring(urlBase.indexOf('/') + 2);
        registry.addMapping("/**")
                .allowedOrigins("https://app." + base, "https://dev." + base, "https://prod." + base, "https://api." + base, "https://pre-prod." + base,
                        "http://localhost:3000")
                .allowCredentials(true)
                .allowedMethods("GET", "POST", "PUT", "DELETE");
    }

}
