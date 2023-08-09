package vizicard.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.urlBase}")
    private String urlBase;

    @Value("${spring.datasource.username}")
    private String databaseUsername;

    @Value("${spring.datasource.password}")
    private String databasePassword;

    @Bean
    public DataSource dataSource() throws UnknownHostException {
        String path;
        if (InetAddress.getLocalHost().getHostName().equals("1560161-cj20879")) {
            path = "prod";
        } else {
            path = "dev";
        }
        return getDataSourceWithPath(path);
    }

    DataSource getDataSourceWithPath(String path) {
        return DataSourceBuilder.create()
                .username(databaseUsername)
                .password(databasePassword)
                .url(urlBase + path)
                .build();
    }
}