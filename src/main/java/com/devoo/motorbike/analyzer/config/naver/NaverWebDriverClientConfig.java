package com.devoo.motorbike.analyzer.config.naver;

import com.devoo.naverlogin.NaverClient;
import com.devoo.naverlogin.exception.NaverLoginFailException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NaverWebDriverClientConfig {
    @Value("${naver.userId}")
    private String userId;

    @Value("${naver.password}")
    private String password;

    @Bean
    public NaverClient naverLogin() throws NaverLoginFailException {
        return new NaverClient().tryLogin(userId, password);
    }
}