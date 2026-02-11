package com.ordertogether.paymentservice.config;

import java.util.Base64;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@TestConfiguration
public class PaymentGatewayTestClientConfig {

    @Value("${pg.toss.url}")
    private String baseUrl;

    @Value("${pg.toss.secret-key}")
    private String secretKey;

    public RestClient createWithHeaders(Map<String, String> headers) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

        return RestClient.builder()
            .requestFactory(factory)
            .baseUrl(baseUrl)
            .defaultHeaders(httpHeaders -> headers.forEach(httpHeaders::add))
            .defaultHeader(HttpHeaders.AUTHORIZATION, createEncodedBasicAuthHeader())
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }

    private String createEncodedBasicAuthHeader() {
        String encodedSecretKey = Base64.getEncoder().encodeToString((secretKey + ":").getBytes());
        return "Basic " + encodedSecretKey;
    }

}
