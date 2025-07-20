package Project.TiengAnhMoiNgay.AWS.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
@ConditionalOnProperty(name = "aws.aurora.test.enabled", havingValue = "true", matchIfMissing = false)
public class TestDatabaseConfig {

    @Bean(name = "testWriterDataSource")
    @ConfigurationProperties("spring.datasource.writer")
    public DataSource testWriterDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "testReaderDataSource")
    @ConfigurationProperties("spring.datasource.reader")
    public DataSource testReaderDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "testDrReaderDataSource")
    @ConfigurationProperties("spring.datasource.dr-reader")
    public DataSource testDrReaderDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "testWriterJdbcTemplate")
    public JdbcTemplate testWriterJdbcTemplate(@Qualifier("testWriterDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "testReaderJdbcTemplate")
    public JdbcTemplate testReaderJdbcTemplate(@Qualifier("testReaderDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "testDrReaderJdbcTemplate")
    public JdbcTemplate testDrReaderJdbcTemplate(@Qualifier("testDrReaderDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
