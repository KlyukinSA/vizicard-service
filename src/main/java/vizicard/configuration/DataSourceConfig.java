package vizicard.configuration;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBVersionAttribute;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import javax.xml.crypto.Data;

@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.urlBase}")
    private String urlBase;

    @Value("${spring.datasource.username}")
    private String databaseUsername;

    @Value("${spring.datasource.password}")
    private String databasePassword;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Bean
    DataSource dataSource() {
        if (activeProfile.equals("prod")) {
            return getDataSourceWithPath("prod");
        }
        return getDataSourceWithPath("dev");
    }

    DataSource getDataSourceWithPath(String path) {
        return DataSourceBuilder.create()
                .username(databaseUsername)
                .password(databasePassword)
                .url(urlBase + path)
                .build();
    }
}