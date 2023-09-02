package vizicard.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

@Component
public class ServerPortCustomizer
        implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Override
    public void customize(ConfigurableWebServerFactory factory) {
        switch (activeProfile) {
            case "prod":
                factory.setPort(8082);
                return;
            case "pre_prod":
                factory.setPort(8081);
                return;
            case "dev":
                factory.setPort(8080);
        }
    }

}
