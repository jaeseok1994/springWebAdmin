package com.webAdmin.security.persistence;

import com.webAdmin.security.domain.model.Auth;
import com.webAdmin.security.domain.repository.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * author:james
 * day:2020-09-12
 */
@Repository
public class AuthRepositoryImpl implements AuthRepository {
    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Override
    public List<Auth> list() {
        return jdbcTemplate.query("select * from authority order by url", createMapper());
    }
    private RowMapper<Auth> createMapper() {
        return BeanPropertyRowMapper.newInstance(Auth.class);
    }
}