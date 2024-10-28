package com.duantn.be_project.Service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;


@Configuration
public class AppConfig {

        @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        
        // Thiết lập timeout kết nối và đọc
        factory.setConnectTimeout(30000); // 30 giây
        factory.setReadTimeout(30000); // 30 giây

        return new RestTemplate(factory);
    }
}
