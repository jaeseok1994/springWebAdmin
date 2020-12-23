package com.webAdmin.security.domain.repository;


import com.webAdmin.security.domain.model.Menu;

import java.util.List;

/**
 * author:james
 * day:2020-09-10
 */
public interface MenuRepository {

    void add(Menu menu);

    void update(Menu menu);

    Menu get(String code);

    boolean contains(String code);

    List<Menu> list();

    void remove(String code);

    void switchStatus(String code,boolean disabled);

    List<Menu> roleMenus(String roleId);

    List<Menu> getNavMenus(String userId);
}
