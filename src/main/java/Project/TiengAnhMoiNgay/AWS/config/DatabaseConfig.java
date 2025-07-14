package Project.TiengAnhMoiNgay.AWS.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DatabaseConfig {

    @Primary
    @Bean(name = "writerDataSource")
    @ConfigurationProperties("spring.datasource.writer")
    public DataSource writerDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "readerDataSource")
    @ConfigurationProperties("spring.datasource.reader")
    public DataSource readerDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "drReaderDataSource")
    @ConfigurationProperties("spring.datasource.dr-reader")
    public DataSource drReaderDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "routingDataSource")
    public DataSource routingDataSource(
            @Qualifier("writerDataSource") DataSource writerDataSource,
            @Qualifier("readerDataSource") DataSource readerDataSource,
            @Qualifier("drReaderDataSource") DataSource drReaderDataSource) {
        
        RoutingDataSource routingDataSource = new RoutingDataSource();
        
        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put("writer", writerDataSource);
        dataSourceMap.put("reader", readerDataSource);
        dataSourceMap.put("dr-reader", drReaderDataSource);
        
        routingDataSource.setTargetDataSources(dataSourceMap);
        routingDataSource.setDefaultTargetDataSource(writerDataSource);
        
        return routingDataSource;
    }

    @Bean(name = "writerJdbcTemplate")
    public JdbcTemplate writerJdbcTemplate(@Qualifier("writerDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "readerJdbcTemplate")
    public JdbcTemplate readerJdbcTemplate(@Qualifier("readerDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "drReaderJdbcTemplate")
    public JdbcTemplate drReaderJdbcTemplate(@Qualifier("drReaderDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
