package com.webAdmin.security.domain.model;

/**
 * author:james
 * day:2020-09-12
 */
public class Auth {
    private String url;
    private String Authority;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAuthority() {
        return Authority;
    }

    public void setAuthority(String authority) {
        Authority = authority;
    }

    @Override
    public String toString() {
        return "Auth{" +
                "url='" + url + '\'' +
                ", Authority='" + Authority + '\'' +
                '}';
    }
}
