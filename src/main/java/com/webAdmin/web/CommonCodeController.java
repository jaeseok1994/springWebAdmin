package com.webAdmin.web;

import com.webAdmin.dao.CommonMybatisDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

/**
 * author:james
 * day:2020-09-12
 */
@Controller
@RequestMapping("/commonCode/")
public class CommonCodeController {
    @Autowired
    private CommonMybatisDao dao;

    @RequestMapping(method = RequestMethod.POST, value = "/selectList.do/{sqlId}")
    public ModelAndView list(@PathVariable("sqlId") String sqlId, @RequestBody HashMap<String,Object> param  ) {

        LinkedHashMap linkedHashMap = (LinkedHashMap)param.get("test");

        param.put("mapperGroup","commonCode");
        param.put("mapperPgm","common");
        param.put("mapperSqlId",sqlId);
        List<Map<?, ?>> result = dao.selectList(param);
        ModelAndView mv = new ModelAndView("jsonView");

        mv.addObject("resultList", result);
        return mv;
    }
}
