package itmo.labs.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = "itmo.labs")
public class DatabaseConfig {
    @Bean
    public DataSource dataSource(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        // for helios dataSource.setUrl("jdbc:postgresql://pg:5432/studs");
        dataSource.setUrl("jdbc:postgresql://pg:5432");
        dataSource.setUsername("your-username");
        dataSource.setPassword("your-password");
        return dataSource;
    }

}
