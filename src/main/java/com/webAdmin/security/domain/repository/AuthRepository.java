package com.webAdmin.security.domain.repository;


import com.webAdmin.security.domain.model.Auth;

import java.util.List;

/**
 * author:james
 * day:2020-09-10
 */
public interface AuthRepository {


    List<Auth> list();

}
