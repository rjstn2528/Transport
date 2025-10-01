// RestTemplateConfig.java - 타임아웃 시간 증가
package net.koreate.transport.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

@Configuration
public class RestTemplateConfig {
    
    @Bean
    public RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        
        // 크롤링을 위한 타임아웃 시간 증가
        factory.setConnectTimeout(20000);  // 연결 타임아웃 20초
        factory.setReadTimeout(60000);     // 읽기 타임아웃 60초 (크롤링 시간 고려)
        
        RestTemplate restTemplate = new RestTemplate(factory);
        
        // UTF-8 인코딩 설정
        restTemplate.getMessageConverters().forEach(converter -> {
            if (converter instanceof org.springframework.http.converter.StringHttpMessageConverter) {
                ((org.springframework.http.converter.StringHttpMessageConverter) converter)
                    .setDefaultCharset(StandardCharsets.UTF_8);
            }
        });
        
        return restTemplate;
    }
}