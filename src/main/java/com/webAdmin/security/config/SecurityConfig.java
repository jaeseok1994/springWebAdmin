package com.webAdmin.security.config;

/**
 * author:james
 * day:2020-09-09
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webAdmin.dao.CommonMybatisDao;
import com.webAdmin.security.domain.model.Auth;
import com.webAdmin.security.domain.repository.AuthRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * author:james
 * day:2020-08-30
 */
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @Autowired
    UserDetailsService userDetailsService;
    @Autowired
    DataSource dataSource;
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //super.configure(auth);
        auth
                //.parentAuthenticationManager(authenticationManagerBean())
                .userDetailsService(userDetailsService);
//        auth.jdbcAuthentication()
//                .dataSource(dataSource)
//                .usersByUsernameQuery("select username,password,1 enabled from users where username = ?")
//                .authoritiesByUsernameQuery("select username,roles authority from user_roles where username = ?")
//
//        ;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(
                "/**/*.js", "/**/*.js.map", "/**/*.ts", "/**/*.css"
                ,"/**/*.css.map", "//*.png", "/**/*.png", "/**/*.gif", "/**/*.jpg", "/**/*.fco"
//                ,"/**/*.woff", "/**/*.woff2", "/**/*.font", "/**/*.svg", "/**/*.ttf"
          //      ,  "/**/*.pdf","/*.ico", "/admin/api/**", "/404", "/401","/403", "/error"
        );

    }

    @Bean//무조건 찾음
    public PasswordEncoder getPasswordEncoder(){
        return NoOpPasswordEncoder.getInstance();
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.headers().frameOptions().disable();
        http.authorizeRequests()
                .antMatchers("/**/*.js", "/**/*.js.map", "/**/*.ts", "/**/*.css"
                        ,"/**/*.css.map", "/**/*.png", "/**/*.gif", "/**/*.jpg", "/**/*.fco"
                        ,"/**/*.woff", "/**/*.woff2", "/**/*.font", "/**/*.svg", "/**/*.ttf"
                        ,  "/**/*.pdf","/*.ico", "/admin/api/**", "/404", "/401","/403", "/error"
                ).permitAll()
                .antMatchers("/","/login","/user/logout","/to-login").permitAll()
                .antMatchers("/user/profile").hasRole("USER")
            //.antMatchers("/**").hasRole("USER")
        ;
        setAntMatchers(http, "ROLE_");

        http.formLogin().loginProcessingUrl("/login").loginPage("/login").defaultSuccessUrl("/")
                .successHandler(new AuthenticationSuccessHandler()).failureHandler(new AuthenticationFailureHandler())
            .and().logout().logoutUrl("/login").logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessHandler(new LogoutSuccessHandler())
            .and().exceptionHandling().authenticationEntryPoint(new MyAuthenticationEntryPoint())
                .accessDeniedHandler(new MyAccessDeniedHandler())
        ;
        //http.addFilterBefore(urlSecurityInterceptor(),FilterSecurityInterceptor.class);//사용자 지정 권한 처리
        //http.addFilterBefore(urlSecurityInterceptor(),FilterSecurityInterceptor.class);//사용자 지정 권한 처리

//            .antMatchers("/user").hasAnyRole("USER","ADMIN")
//            .antMatchers("/admin").hasRole("ADMIN")
//            .antMatchers("/**").hasRole("USER")

    }
    protected boolean isAjax(HttpServletRequest request) {
        return StringUtils.isNotBlank(request.getHeader("X-Requested-With"));
    }

    /* addFilterAt 필터 적용시필요  */
//    @Bean
//    public UrlSecurityInterceptor urlSecurityInterceptor() {
//        return new UrlSecurityInterceptor();
//    }
//
////    @Bean
////    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
////        return configuration.getAuthenticationManager();
////    }
//    @Bean
//    public AuthenticationManager authenticationManager() throws Exception {
//        return super.authenticationManager();
//    }
//    @Bean
//    protected AccessDecisionManager accessDecisionManager() {
//        RoleVoter roleVoter = new RoleVoter();
//        roleVoter.setRolePrefix("");
//        List voters = new ArrayList<>();
//        voters.add(roleVoter);
//        AccessDecisionManager accessDecisionManager = new AffirmativeBased(voters);
//        return accessDecisionManager;
//    }
    /* addFilterAt 필터 적용시필요  */

    private class LogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

        @Override
        public void onLogoutSuccess(HttpServletRequest request,
                                    HttpServletResponse response, Authentication authentication)
                throws IOException, ServletException {
            if (!isAjax(request)) {
                super.onLogoutSuccess(request, response, authentication);
            }
        }
    }
    private class MyAuthenticationEntryPoint implements AuthenticationEntryPoint {

        @Override
        public void commence(HttpServletRequest request,
                             HttpServletResponse response,
                             AuthenticationException authException) throws IOException {
            response.setCharacterEncoding("utf-8");
            if (isAjax(request)) {
                response.getWriter().println("\n" + "로그인 해주세요");
            } else {
                response.sendRedirect("/login");
            }

        }
    }
    private class MyAccessDeniedHandler implements AccessDeniedHandler {
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/html;charset=UTF-8");
            if (isAjax(request)) {

                ObjectMapper mapper = new ObjectMapper();
                Map<String, String> map = new HashMap<>();
                map.put("error", "access denied");
                map.put("message", "액세스 할 권한이 없습니다.");
                String json = mapper.writeValueAsString(map);

                //response.getWriter().println("액세스 할 권한이 없습니다.");
                response.getWriter().println(json);
            } else {
                response.sendRedirect("/403");
            }

        }
    }
    private class AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

        @Override
        public void onAuthenticationSuccess(HttpServletRequest request,
                                            HttpServletResponse response, Authentication authentication)
                throws ServletException, IOException {

            clearAuthenticationAttributes(request);
            if (!isAjax(request)) {
                super.onAuthenticationSuccess(request, response, authentication);
            }
        }
    }
    private class AuthenticationFailureHandler implements org.springframework.security.web.authentication.AuthenticationFailureHandler {

        @Override
        public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
            String username = request.getParameter("username");
            String msg = exception.getMessage();

            request.setAttribute("msg",msg);
            request.setAttribute("username",username);

            logger.debug(msg);
            logger.debug(username);

            //로그인 실패시 필요한 작업 추가

            response.sendRedirect("/login?msg="+msg);
        }
    }
    @Autowired
    private CommonMybatisDao dao;

    @Autowired
    protected AuthRepository authRepository;
    protected void setAntMatchers(HttpSecurity http, String rolePrefix) throws Exception {

        HashMap<String, Object> param = new HashMap<>();
        param.put("mapperGroup","webAdmin");
        param.put("mapperPgm","security");
        param.put("mapperSqlId","selectUrlRoleList");
        List<Map<?, ?>> result = dao.selectList(param);


        for(Map<?, ?> role : result) {
            String url = (String)role.get("URL");
            logger.info(url);
            String role_id = (String)role.get("ROLE_ID");
            logger.info(role_id);
            String[] roles = role_id.split(",");

            for(int i = 0; i < roles.length; i++) {
                roles[i] = roles[i].toUpperCase();
            }
            // DB에서 url을 읽어올 때 앞에 '/'이 없다면 앞에 '/'를 넣어준다.
            if(url.charAt(0) != '/') {
                url = "/" + url;
            }
            http.authorizeRequests()
                    .antMatchers(url)
                    .hasAnyAuthority(roles);
            //System.out.println(url);
            logger.info(url);
            for(String s : roles) {
                //System.out.println(s);
                logger.info(s);
            }
        }
//        List<Auth> list = authRepository.list();
//
//        System.out.println(list);
//        String pre_auth = "";
//        String pre_url = "";
//        for(Auth auth : list) {
//
//            String[] roles = auth.getAuthority().split(",");
//
//            for(int i = 0; i < roles.length; i++) {
//                roles[i] = roles[i].toUpperCase();
//            }
//            // DB에서 url을 읽어올 때 앞에 '/'이 없다면 앞에 '/'를 넣어준다.
//            String url = auth.getUrl();
//            if(url.charAt(0) != '/') {
//                url = "/" + url;
//            }
//            http.authorizeRequests()
//                    .antMatchers(url)
//                    .hasAnyAuthority(roles);
//            //System.out.println(url);
//            logger.info(url);
//            for(String s : roles) {
//                //System.out.println(s);
//                logger.info(s);
//            }
//        }

        http.authorizeRequests()
                .antMatchers("/**").permitAll()	// 넓은 범위의 URL을 아래에 배치한다.
                .anyRequest().authenticated();

    }
}
