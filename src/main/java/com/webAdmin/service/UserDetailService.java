package com.webAdmin.service;

import com.webAdmin.dao.CommonMybatisDao;
import com.webAdmin.security.domain.model.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * author:james
 * day:2020-09-10
 */
@Service
public class UserDetailService implements UserDetailsService {

    @Autowired
    private CommonMybatisDao dao;

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        HashMap<String, Object> param = new HashMap<>();
        param.put("mapperGroup","webAdmin");
        param.put("mapperPgm","security");
        param.put("mapperSqlId","selectUserList");
        param.put("id",id);
        List<Map<?, ?>> result = dao.selectList(param);

        if(result==null || result.size()==0){
            throw new UsernameNotFoundException("no user");
        }
        HashMap<String,String>  user = (HashMap<String,String>)result.get(0);
        HashMap  user2 = (HashMap)result.get(0);
        System.out.println(user2.get("DISABLED").equals("0"));
        System.out.println(user2.get("DISABLED").toString().equals("0"));
//        System.out.println(user.get("DISABLED"));
//        System.out.println(user.get("DISABLED").equals("0"));
        Object dis = result.get(0).get("DISABLED");
        boolean disabled = (dis.toString()+"").equals("0");
        SecurityUser userDetails = new SecurityUser(user.get("ID"), user.get("USERNAME"),user.get("PASSWORD"),disabled, true, true, true, grantedAuthorities(user.get("ID")), user.get("SALT"), user.get("EMAIL"));
//        SecurityUser userDetails = new SecurityUser(user.get("id"), user.get("id")username, user.getPassword(), !user.isDisabled(), true, true, true, grantedAuthorities(user.getUsername()), user.getSalt(), user.getEmail());
        return userDetails;
    }

    protected Collection<GrantedAuthority> grantedAuthorities(String id){

        HashMap<String, Object> param = new HashMap<>();
        param.put("mapperGroup","webAdmin");
        param.put("mapperPgm","security");
        param.put("mapperSqlId","selectUserRoleList");
        param.put("id",id);
        List<Map<?, ?>> result = dao.selectList(param);

        if(CollectionUtils.isEmpty(result)){
            return new ArrayList<>();
        }
        Collection<GrantedAuthority> authorities=new HashSet<>();
        //비활성화 된 역할 무시
        //result.stream().filter(role -> !role.isDisabled()).forEach((resource -> {
        result.stream().forEach(
                        resource -> {
                            authorities.add(new SimpleGrantedAuthority((String) resource.get("NAME")));
                        }
                );
        return authorities;
    }

}
