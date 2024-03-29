package com.webAdmin.security.persistence;

import com.webAdmin.security.domain.model.User;
import com.webAdmin.security.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * author:james
 * day:2020-09-10
 */
@Repository
public class UserRepositoryImpl implements UserRepository {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Override
    public void add(User user) {
        jdbcTemplate.update("INSERT users (id,username,password,email,disabled,createTime,salt) VALUES (?,?,?,?,?,?,?)",user.getId(),user.getUsername(),user.getPassword(),user.getEmail(),user.isDisabled()?1:0,new Date(),user.getSalt());
    }

    @Override
    public void update(User user) {
        jdbcTemplate.update("UPDATE users SET email=? ,password = case when ? = '' then PASSWORD ELSE ? end  WHERE username=?",
                user.getEmail(),
                user.getPassword(),
                user.getPassword(),
                user.getUsername());
    }

    @Override
    public void updateRoles(String uid, List<String> rids) {
        jdbcTemplate.update("DELETE FROM user_roles WHERE username=?", uid);
        if (!CollectionUtils.isEmpty(rids)) {
            jdbcTemplate.batchUpdate("INSERT user_roles (username,roles) VALUES (?,?)", new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setString(1, uid);
                    ps.setString(2, rids.get(i));
                }

                @Override
                public int getBatchSize() {
                    return rids.size();
                }
            });
        }
    }

    @Override
    public User get(String id) {
        return jdbcTemplate.queryForObject("select * from users where id=?", BeanPropertyRowMapper.newInstance(User.class),id);
    }

    @Override
    public Integer max() {
        return jdbcTemplate.queryForObject("select max(id) from users ",Integer.class);
    }
    @Override
    public boolean contains(String name) {
        return jdbcTemplate.query("select count(username) from users where username=?", rs -> rs.getInt(1)>0,name);
    }

    @Override
    public List<User> list() {
        return jdbcTemplate.query("select * from users where username <> 'root'", BeanPropertyRowMapper.newInstance(User.class));
    }


    @Override
    public boolean hasResourcePermission(String uid,String resourceCode) {
        return jdbcTemplate.query("select count(*) from user_role ur join role_resource rr on ur.role_id=rr.role_id where ur.uid=? and rr.resource_id=?",rs -> rs.getInt(0)>0,uid,resourceCode);
    }



    @Override
    public void remove(String id) {
        User user=get(id);
        if(user.isRoot()){
            return;
        }
        jdbcTemplate.update("DELETE FROM users WHERE id=?",id);
        jdbcTemplate.update("DELETE FROM user_role WHERE uid=?",id);
    }

    public void switchStatus(String id,boolean disabled){
        jdbcTemplate.update("update users SET disabled=? WHERE id=?",disabled?1:0,id);
    }


    @Override
    public User findByUserName(String username) {
        try {
            return jdbcTemplate.queryForObject("select * from users where username=? ", BeanPropertyRowMapper.newInstance(User.class), username);
        }catch (EmptyResultDataAccessException e){
            return null;
        }
    }

    @Override
    public List<User> getUserByUname(String username) {
        return jdbcTemplate.query("select * from users where username =?",BeanPropertyRowMapper.newInstance(User.class),username);
    }

    @Override
    public List<User> getUserByEmail(String email) {
        return jdbcTemplate.query("select * from users  where email =?",BeanPropertyRowMapper.newInstance(User.class),email);
    }

}
