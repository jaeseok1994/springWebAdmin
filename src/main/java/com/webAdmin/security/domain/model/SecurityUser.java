package com.webAdmin.security.domain.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * author:james
 * day:2020-09-10
 */
public class SecurityUser extends User {
    private String uid;
    private String salt;
    private String email;

    private String currentdate;

    public SecurityUser(String uid, String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities, String salt, String email,String currentdate) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.salt = salt;
        this.uid=uid;
        this.email = email;
        this.currentdate = currentdate;
    }

    public String getSalt() {
        return salt;
    }

    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }

    public String getCurrentdate() {
        return currentdate;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
