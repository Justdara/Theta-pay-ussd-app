package com.thetapay.ussdapp.thetaRestClient;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ThetaRestClient {

    private static final RestTemplate restTemplate = new RestTemplate();

    public static <T> T post(String url, Object requestObject, Class<T> responseType) {
        var headers = getHeaders();
        var entity = new HttpEntity<>(requestObject, headers);
        var responseEntity = restTemplate.postForEntity(url, entity, responseType);
        return responseEntity.getBody();
    }

    private static HttpHeaders getHeaders(){
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
