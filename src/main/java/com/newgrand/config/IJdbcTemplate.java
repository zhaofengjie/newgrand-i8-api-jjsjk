package com.newgrand.config;

import java.util.Collection;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;

import javax.sql.DataSource;

/**
 * 功能：重写jdbcTempLate工具,
 *  覆盖queryForObject报org.springframework.dao.EmptyResultDataAccessException:
 Incorrect result size: expected 1, actual 0 错误
 * @Author ChenXiangLu
 * @Date 2020/12/7 21:37
 * @Version 1.0
 */

public class IJdbcTemplate extends JdbcTemplate {

    public IJdbcTemplate(DataSource dsOrcle) {
        super(dsOrcle);
    }

    @Override
    public <T> T queryForObject(String sql, RowMapper<T> rowMapper) throws DataAccessException {
        List<T> results = this.query(sql, rowMapper);
        return IJdbcTemplate.requiredSingleResult(results);
    }
    @Override
    public <T> T queryForObject(String sql,  RowMapper<T> rowMapper,Object[] args) throws DataAccessException {
        List<T> results = query(sql, args, new RowMapperResultSetExtractor<T>(rowMapper, 1));
        return IJdbcTemplate.requiredSingleResult(results);
    }
    public static <T> T requiredSingleResult(Collection<T> results) throws IncorrectResultSizeDataAccessException {
        int size = (results != null ? results.size() : 0);
        if (size == 0) { return null; } if (results.size() > 1)
        { throw new IncorrectResultSizeDataAccessException(1, size); }
        return results.iterator().next();
    }
}