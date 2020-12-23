package com.webAdmin.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * author:james
 * day:2020-09-10
 */

@ControllerAdvice
public class PublicAdvice {

    @ExceptionHandler
    public void handleControllerException(HttpServletRequest request, HttpServletResponse response, Throwable ex) throws IOException {
        //ex.printStackTrace();
        String ajax = request.getHeader("X-Requested-With");
        response.setCharacterEncoding("utf-8");
        if (StringUtils.isBlank(ajax)) {
            response.sendRedirect("/error");
        } else {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> map = new HashMap<>();
            map.put("error", ex.toString());
            map.put("message", ex.getMessage());
            String json = mapper.writeValueAsString(map);

            //response.getWriter().println("액세스 할 권한이 없습니다.");
            response.getWriter().println(json);
            //response.getWriter().println("Error:" + ex.getMessage());
        }

    }

//    @ModelAttribute
//    public void addCommonModel(Model model, HttpServletRequest request) {
//        //SecurityUser user = SecurityUtil.getUser();
//        String ajax = request.getHeader("X-Requested-With");
//        if(ajax != null && !ajax.equals("")) return;
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if(authentication != null){
//            Object o = authentication.getPrincipal();
//            // SecurityConfig 에서 jdbcAuthentication 사용할시
////            if(o instanceof User) {
////                User user = (User) o;
////                if (user != null) {
////                    System.out.println(user);
////                    model.addAttribute("user", user);
////                    model.addAttribute("navs", menuService.getNavMenus(user.getUsername()));
////                }
////            }else
//            if(o instanceof UserDetails) {
//                UserDetails user = (UserDetails) o;
//                if (user != null) {
//                    System.out.println(user);
//                    model.addAttribute("user", user);
//                    model.addAttribute("navs", menuService.getNavMenus(user.getUsername()));
//                }
//            }else{
//                System.out.println(o);
//            }
//
//        }
//
//    }

}
