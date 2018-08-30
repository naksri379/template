package com.mytest.nack.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class EndpointHelper {
    @Autowired
    private TestRestTemplate restTemplate;

    public ResponseEntity<String> callHelloWorld() {
        HttpHeaders requestHeaders = new HttpHeaders();
        HttpEntity request = new HttpEntity(requestHeaders);
        return this.restTemplate.exchange("/hello", HttpMethod.GET, request, String.class);
    }
}
