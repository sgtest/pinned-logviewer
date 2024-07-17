package com.so;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.hazelcast.HazelcastAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;


@EnableAutoConfiguration(exclude = { DataSourceTransactionManagerAutoConfiguration.class,
        DataSourceAutoConfiguration.class, HazelcastAutoConfiguration.class})
@SpringBootApplication()
@MapperScan("com.so.mapper")
public class Application1 extends org.springframework.boot.web.servlet.support.SpringBootServletInitializer{
	
	private static Logger logger = LoggerFactory.getLogger(Application1.class);
	
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(Application1.class); 
        ConfigurableApplicationContext ctx = app.run(args);
		logger.info("the application start success!!!");
	}
	
	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application1.class);
    }
}
