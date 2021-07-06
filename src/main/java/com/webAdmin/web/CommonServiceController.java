package com.webAdmin.web;

import com.webAdmin.dao.CommonMybatisDao;
import com.webAdmin.security.domain.model.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
@RequestMapping("/service/{group}/{pgm}")
public class CommonServiceController {
    @Autowired
    private CommonMybatisDao dao;

    /* http://localhost:8080/service/system/authMenu/selectForm.do
     */
    @RequestMapping(method = RequestMethod.GET, value = "/selectForm.do")
    public String list(@PathVariable("group") String group, @PathVariable("pgm") String pgm, Model model){
        //model.addAttribute("list",userService.list());
        return "/"+group+"/"+pgm+"";
    }
    @RequestMapping(method = RequestMethod.POST, value = "/selectList.do/{sqlId}")
    public ModelAndView list(@PathVariable("group") String group,@PathVariable("pgm") String pgm,@PathVariable("sqlId") String sqlId,  @RequestBody HashMap<String,Object> param  ) {

        //LinkedHashMap linkedHashMap = (LinkedHashMap)param.get("test");

        param.put("mapperGroup",group);
        param.put("mapperPgm",pgm);
        param.put("mapperSqlId",sqlId);
        List<Map<?, ?>> result = dao.selectList(param);
        ModelAndView mv = new ModelAndView("jsonView");

        mv.addObject("resultList", result);
        return mv;
    }
    @RequestMapping(method = RequestMethod.POST, value = "/insertList")
    public ModelAndView insertList(@PathVariable("group") String group,@PathVariable("pgm") String pgm, String func, @RequestBody HashMap<String,Object> param  ) {
        return maintList(group,pgm,"insertList",param);
    }
    @RequestMapping(method = RequestMethod.POST, value = "/saveList")
    public ModelAndView saveList(@PathVariable("group") String group,@PathVariable("pgm") String pgm,  @RequestBody HashMap<String,Object> param  ) {
        return maintList(group,pgm,"saveList",param);
    }
    @RequestMapping(method = RequestMethod.POST, value = "/updateList")
    public ModelAndView updateList(@PathVariable("group") String group,@PathVariable("pgm") String pgm,  @RequestBody HashMap<String,Object> param  ) {
        return maintList(group,pgm,"updateList",param);
    }
    @RequestMapping(method = RequestMethod.POST, value = "/deleteList")
    public ModelAndView deleteList(@PathVariable("group") String group,@PathVariable("pgm") String pgm,  @RequestBody HashMap<String,Object> param  ) {
        return maintList(group,pgm,"deleteList",param);
    }
    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView userMaintList(@PathVariable("group") String group,@PathVariable("pgm") String pgm,  @RequestBody HashMap<String,Object> param  ) {
        String func = (String)param.get("mapId");
        return maintList(group,pgm,func,param);
    }
    @RequestMapping(method = RequestMethod.POST, value = "/maintList.do/{sqlId}")
    public ModelAndView maintList(@PathVariable("group") String group,@PathVariable("pgm") String pgm,@PathVariable("sqlId") String sqlId,  @RequestBody HashMap<String,Object> param  ) {

        LinkedHashMap linkedHashMap = (LinkedHashMap)param.get("test");

        param.put("mapperGroup",group);
        param.put("mapperPgm",pgm);
        param.put("mapperSqlId",sqlId);

        ArrayList<Map> list = (ArrayList<Map>)param.get("list");

        int count = 0;
        for(Map map:list){
            //System.out.println(map);
            initParam(map);
            count += dao.maint(param,map);
        }
        //List<Map<String, Object>> result = dao.selectList(param);
        ModelAndView mv = new ModelAndView("jsonView");

        mv.addObject("resultCount", count+"");
        setMessage(mv,sqlId);
        return mv;
    }
    @RequestMapping(method = RequestMethod.POST, value = "/maintList2.do/{sqlId}")
    public ModelAndView maintList2(@PathVariable("group") String group,@PathVariable("pgm") String pgm,@PathVariable("sqlId") String sqlId,  @RequestBody HashMap<String,Object> param  ) {

        LinkedHashMap linkedHashMap = (LinkedHashMap)param.get("test");

        param.put("mapperGroup",group);
        param.put("mapperPgm",pgm);
        param.put("mapperSqlId",sqlId);

        int count = 0;
        //System.out.println(map);
        initParam(param);
        count += dao.maint2(param);

        //List<Map<String, Object>> result = dao.selectList(param);
        ModelAndView mv = new ModelAndView("jsonView");

        mv.addObject("resultCount", count+"");
        setMessage(mv,sqlId);
        return mv;
    }

    private void setMessage(ModelAndView mv,String sqlId) {
        if (sqlId.startsWith("select")) {
            mv.addObject("message", "조회되었습니다.");
        } else if (sqlId.startsWith("insert")) {
            mv.addObject("message", "입력이 되었습니다.");
        } else if (sqlId.startsWith("maint")) {
            mv.addObject("message", "저장이 되었습니다.");
        } else if (sqlId.startsWith("update")) {
            mv.addObject("message", "수정이 되었습니다.");
        } else if (sqlId.startsWith("delete")) {
            mv.addObject("message", "삭제이 되었습니다.");
        }
    }
    private void initParam(Map map){
        String userId = "";
        SecurityUser user = getSecurityUser();
        if(user != null){
            userId = user.getUid();
        }
        map.put("_loginId_",userId);
    }
    private void initParam(HashMap<String,Object> param){
        String userId = "";
        SecurityUser user = getSecurityUser();
        if(user != null){
            userId = user.getUid();
        }
        param.put("_loginId_",userId);
    }
    private SecurityUser getSecurityUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String userId = "";
        if(auth != null){
            Object o = auth.getPrincipal();
            if(o instanceof SecurityUser){
                SecurityUser user = (SecurityUser)auth.getPrincipal();
                return user;
            }
        }
        return null;
    }
}
