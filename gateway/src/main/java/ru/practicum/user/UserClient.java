package ru.practicum.user;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@Transactional
public class UserClient {

    public ResponseEntity<Object> findAllUsers() {

        ResponseEntity<Object> response = new RestTemplate().getForEntity("http://localhost:9090/users", Object.class);

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }

    public ResponseEntity<Object> findUserById(int userId) {

        HashMap<String, Integer> params = new HashMap<>(Map.of("userId", userId));

        ResponseEntity<Object> response;

        try {
            response = new RestTemplate().getForEntity("http://localhost:9090/users/{userId}", Object.class, params);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }

    public ResponseEntity<Object> createUser(UserDto newUser) {

        ResponseEntity<Object> response;

        try {
            response = new RestTemplate().postForEntity("http://localhost:9090/users", newUser, Object.class);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }

    public Object updateUser(UserDto newUser, int userId) {

        HashMap<String, Integer> params = new HashMap<>(Map.of("userId", userId));
        HttpEntity<UserDto> httpEntity = new HttpEntity<>(newUser);

        ResponseEntity<Object> response;

        try {
            response = restTemplate().exchange("http://localhost:9090/users/{userId}", HttpMethod.PATCH, httpEntity, Object.class, params);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }

    public void deleteUser(int userId) {

        HashMap<String, Integer> params = new HashMap<>(Map.of("userId", userId));

        new RestTemplate().delete("http://localhost:9090/users/{userId}", params);
    }

    private RestTemplate restTemplate() {

        RestTemplate restTemplate = new RestTemplate();
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        restTemplate.setRequestFactory(requestFactory);

        return restTemplate;
    }
}
