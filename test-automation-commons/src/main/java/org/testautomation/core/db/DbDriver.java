package org.testautomation.core.db;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

/**
 * Low-level JDBC wrapper. Prefer using Spring Data JPA repositories for most queries;
 * use this for raw SQL when JPA is too heavy (e.g. bulk reads, non-entity queries).
 */
@Component
public class DbDriver {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driver-class-name:org.postgresql.Driver}")
    private String driverClassName;

    public DataSource dataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName(driverClassName);
        ds.setUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        return ds;
    }

    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }

    public <T> T queryForObject(String sql, Class<T> type, Object... args) {
        return jdbcTemplate().queryForObject(sql, type, args);
    }

    public <T> List<T> queryForList(String sql, Class<T> type, Object... args) {
        return jdbcTemplate().queryForList(sql, type, args);
    }

    public int update(String sql, Object... args) {
        return jdbcTemplate().update(sql, args);
    }
}
