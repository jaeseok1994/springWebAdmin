package com.webAdmin.security.domain.repository;


import com.webAdmin.security.domain.model.User;

import java.util.List;

/**
 * author:james
 * day:2020-09-10
 */
public interface UserRepository {
    void add(User user);
    Integer max();

    void update(User user);

    void updateRoles(String uid, List<String> rids);

    User get(String id);

    boolean contains(String name);

    List<User> list();

    boolean hasResourcePermission(String userId,String resourceCode);

    void remove(String id);

    void switchStatus(String id,boolean disabled);

    User findByUserName(String username);

    List<User> getUserByUname(String username);

    List<User> getUserByEmail(String email);

}
