package com.webAdmin.dao;

/**
 * Created by Leo.
 * User: notho
 * Date: 2020-09-26, Time: 오후 2:45
 */


import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CommonMybatisDao {

    protected static final String NAMESPACE = "proj.";

    @Autowired
    private SqlSession sqlSession;

    public String selectOne() {
        return sqlSession.selectOne(NAMESPACE + "selectName");
    }

    public List<Map<?, ?>> selectList(HashMap<String, Object> param) {
        String group = (String)param.get("mapperGroup");
        String pgm = (String)param.get("mapperPgm");
        String sqlId = (String)param.get("mapperSqlId");
        List<Map<?, ?>> result = sqlSession.selectList(NAMESPACE + group + "." + pgm + "." + sqlId,param);
        return result;
    }
    public int maint(HashMap<String, Object> param,Map map) {
        String group = (String)param.get("mapperGroup");
        String pgm = (String)param.get("mapperPgm");
        String sqlId = (String)param.get("mapperSqlId");
        int result = sqlSession.update(NAMESPACE + group + "." + pgm + "." + sqlId,map);
        return result;
    }
    public int maintOne(HashMap<String, Object> param) {
        String group = (String)param.get("mapperGroup");
        String pgm = (String)param.get("mapperPgm");
        String sqlId = (String)param.get("mapperSqlId");
        int result = sqlSession.update(NAMESPACE + group + "." + pgm + "." + sqlId,param);
        return result;
    }
}
