package com.webAdmin.security.persistence;

import com.webAdmin.security.domain.model.Menu;
import com.webAdmin.security.domain.repository.MenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * author:james
 * day:2020-09-10
 */
@Repository
public class MenuRepositoryImpl implements MenuRepository {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Override
    public void add(Menu menu) {
        jdbcTemplate.update("INSERT menu (id,label,path,`level`,`order`,`url`,`type`,`style`,`disabled`) VALUES (?,?,?,?,?,?,?,?,?)", menu.getId(), menu.getLabel(), menu.getPath(), menu.getLevel(), menu.getOrder(), menu.getUrl(), menu.getType(), menu.getStyle(), menu.isDisabled() ? 1 : 0);
    }

    @Override
    public void update(Menu menu) {
        jdbcTemplate.update("update menu SET label=?,`order`=?,url=?,disabled=?,`type`=?,`style`=? WHERE id=?", menu.getLabel(), menu.getOrder(), menu.getUrl(), menu.isDisabled() ? 1 : 0, menu.getType(), menu.getStyle(), menu.getId());
    }

    @Override
    public Menu get(String id) {
        return jdbcTemplate.queryForObject("select * from menu where id=?",createMapper(),id);
    }

    @Override
    public boolean contains(String id) {
        return jdbcTemplate.queryForObject("select count(id) from menu where id=?",Integer.class,id)>0;
    }

    @Override
    public List<Menu> list() {
        return jdbcTemplate.query("select * from menu order by level,'order'",createMapper());
    }

    @Override
    public void remove(String id) {       //
        jdbcTemplate.update("DELETE FROM menu WHERE id=?",id);
    }

    public void switchStatus(String id,boolean disabled){ //
        jdbcTemplate.update("update menu SET disabled=? WHERE id=?",disabled?1:0,id);
    }

    private RowMapper<Menu> createMapper() {
//        return (rs, rowNum) -> {
//            Menu menu = new Menu();
//            menu.setId(rs.getString("id"));
//            menu.setLabel(rs.getString("label"));
//            menu.setUrl(rs.getString("url"));
//            menu.setDisabled(rs.getBoolean("disabled"));
//            menu.setPath(rs.getString("path"));
//            menu.setOrder(rs.getInt("order"));
//            menu.setType(rs.getInt("type"));
//            menu.setStyle(rs.getString("style"));
//            return menu;
//        };
        return BeanPropertyRowMapper.newInstance(Menu.class);
    }

    @Override
    public List<Menu> roleMenus(String roleId) {
        StringBuffer sb = new StringBuffer();
        sb.append("select m.* \n");
        sb.append("  from menu m join role_menu rm \n");
        sb.append("    on m.id=rm.menu_id \n");
        sb.append(" where rm.role_id = (SELECT NAME FROM role WHERE id = ?) \n");
        return jdbcTemplate.query(
                sb.toString(),createMapper(), roleId);
    }

    @Override
    public List<Menu> getNavMenus(String userId) {
        StringBuffer sb = new StringBuffer();

        sb.append("SELECT m.* \n");
        sb.append("  FROM menu m  \n");
        sb.append(" WHERE id IN (SELECT menu_id FROM role_menu \n");
        sb.append("        WHERE role_id IN ( \n");
        sb.append("                SELECT roles FROM user_roles \n");
        sb.append("                WHERE username = ? \n");
        sb.append("       ) \n");
        sb.append(") \n");
        return jdbcTemplate.query(
                sb.toString(),
                createMapper(),userId);
    }
}
