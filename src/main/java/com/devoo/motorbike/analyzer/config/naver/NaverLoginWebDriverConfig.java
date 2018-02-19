package com.devoo.motorbike.analyzer.config.naver;

import com.devoo.naverlogin.NaverLogin;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NaverLoginWebDriverConfig {
    @Value("${naver.userId}")
    private String userId;

    @Value("${naver.password}")
    private String password;

    @Bean
    public WebDriver naverLogin() {
        return new NaverLogin().tryLogin(userId, password);
    }
}