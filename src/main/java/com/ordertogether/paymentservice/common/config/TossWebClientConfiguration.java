package com.ordertogether.paymentservice.common.config;

import java.time.Duration;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class TossWebClientConfiguration {

    private static final int CONNECTION_TIMEOUT_SECONDS = 3;

    private static final int READ_TIMEOUT_SECONDS = 10;

    @Value("${pg.toss.url}")
    private String baseUrl;

    @Value("${pg.toss.secret-key}")
    private String secretKey;

    @Bean
    public RestClient tossPaymentRestClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(CONNECTION_TIMEOUT_SECONDS));
        factory.setReadTimeout(Duration.ofSeconds(READ_TIMEOUT_SECONDS));

        return RestClient.builder()
            .requestFactory(factory)
            .baseUrl(baseUrl)
            .defaultHeader(HttpHeaders.AUTHORIZATION, createEncodedBasicAuthHeader())
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }

    private String createEncodedBasicAuthHeader() {
        String encodedSecretKey = Base64.getEncoder().encodeToString((secretKey + ":").getBytes());
        return "Basic " + encodedSecretKey;
    }
}
