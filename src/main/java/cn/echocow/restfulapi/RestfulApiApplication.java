package cn.echocow.restfulapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

/**
 * 项目启动类
 *
 * @author Echo
 * @version 1.0
 * @date 2019-01-05 21:27
 */
@SpringBootApplication
@EnableAuthorizationServer
@EnableResourceServer
public class RestfulApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestfulApiApplication.class, args);
    }

}

