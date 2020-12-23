package com.webAdmin.security.domain.repository;


import com.webAdmin.security.domain.model.Role;

import java.util.List;

/**
 * author:james
 * day:2020-09-10
 */
public interface RoleRepository {

    void add(Role role);

    Integer max();

    void update(Role role);

    void updateMenus(String rid, List<String> mids);

    void updateResources(String rid, List<String> resources);

    boolean contains(String roleName);

    Role get(String id);

    List<Role> list();

    void remove(String id);

    void removeRoleMenuByMenuId(String menuId);

    void removeRoleResourceByResourceId(String resourceId);

    void switchStatus(String id,boolean disabled);

    List<Role> getRoles(String userId);


}
