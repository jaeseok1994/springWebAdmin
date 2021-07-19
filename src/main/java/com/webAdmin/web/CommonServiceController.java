package com.webAdmin.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webAdmin.dao.CommonMybatisDao;
import com.webAdmin.security.domain.model.SecurityUser;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
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
    /* http://localhost:8080/service/system/authMenu/selectForm.do
     */
    @RequestMapping(method = RequestMethod.GET, value = "/selectForm.do")
    public String list(@RequestParam Map<String, String> map, @PathVariable("group") String group, @PathVariable("pgm") String pgm, ModelMap model) throws IOException {
        //model.addAttribute("list",userService.list());
        model.addAttribute("group",group);

        Map<String, Object> para = null;
        String param = String.valueOf((map.get("param")));

        param = param.replaceAll("&quot","\"");

        try{
            para = new ObjectMapper().readValue(param, HashMap.class);
        }catch (Exception e ){

            throw new IOException("appData JSON Parae 예외 발생");
        }


        model.addAttribute("para",para);

        SecurityUser user = getSecurityUser();
        String loginId = "";
        String role = "";
        String currentdate = "";
        if(user != null){
            loginId = user.getUid();
            role = user.getAuthorities().toString();
            currentdate = user.getCurrentdate();
        }
        model.addAttribute("loginId",loginId);
        model.addAttribute("roles",role);
        model.addAttribute("currentdate",currentdate);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String userId = "";
        if(auth != null){
            Object o = auth.getAuthorities();
            if(o instanceof SecurityUser){
                //return user;
            }
        }
        return group+"/"+pgm+"";
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
            map.put("map",param.get("map"));
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
    @RequestMapping(method = RequestMethod.POST, value = "/maintOne.do/{sqlId}")
    public ModelAndView maintOne(@PathVariable("group") String group,@PathVariable("pgm") String pgm,@PathVariable("sqlId") String sqlId,  @RequestBody HashMap<String,Object> param  ) {

        LinkedHashMap linkedHashMap = (LinkedHashMap)param.get("test");

        param.put("mapperGroup",group);
        param.put("mapperPgm",pgm);
        param.put("mapperSqlId",sqlId);

        int count = 0;
        //System.out.println(map);
        initParam(param);
        count += dao.maintOne(param);

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
