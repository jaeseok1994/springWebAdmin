package com.webAdmin;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.sql.DataSource;

@SpringBootApplication
public class WebAdminApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebAdminApplication.class, args);
//		SpringApplication app = new SpringApplication(BootAdminApplication.class);
//		app.setAddCommandLineProperties(false);
//		app.run(args);
	}
	@Bean
	public MultipartResolver multipartResolver() {
		return new CommonsMultipartResolver();
	}
	@Bean
	public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
		SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
		sessionFactory.setDataSource(dataSource);
		Resource res = new PathMatchingResourcePatternResolver().getResource("classpath:/config/mybatis-config.xml");
		sessionFactory.setConfigLocation(res);

//		sessionFactory.getConfiguration().setJdbcTypeForNull(JdbcType.NULL);

		Resource[] res2 = new PathMatchingResourcePatternResolver().getResources("classpath:mappers/**/*Mapper.xml");
		sessionFactory.setMapperLocations(res2);
		sessionFactory.setTypeAliasesPackage("com.webAdmin.dao");

		return sessionFactory.getObject();
	}
}
