package cn.echocow.restfulapi;

import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author Echo
 * @version 1.0
 * @date 2019-01-08 22:55
 */
public class PasswordTest {
    @Test
    public void password(){
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode("123456");
        System.out.println(encode);
    }
}
