package com.newgrand.secdev.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.List;

@Configuration
public class JdbcTemplateConfig {
    @Bean
    IJdbcTemplate jdbcTemplateOrcle(@Qualifier("dsOrcle") DataSource dsOrcle) {
        return new IJdbcTemplate(dsOrcle);
    }

}
