package ru.practicum.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${shareit-server.url:http://localhost:9090}")
    String serverUrl;

    private final RestTemplate rest;

    public UserClient() {
        this.rest = new RestTemplate();
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        rest.setRequestFactory(requestFactory);
    }

    public ResponseEntity<Object> findAllUsers() {

        ResponseEntity<Object> response = rest.getForEntity(serverUrl + "/users", Object.class);

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
            response = rest.getForEntity(serverUrl + "/users/{userId}", Object.class, params);
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
            response = rest.postForEntity(serverUrl + "/users", newUser, Object.class);
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
            response = rest.exchange(serverUrl + "/users/{userId}", HttpMethod.PATCH, httpEntity, Object.class, params);
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

        rest.delete(serverUrl + "/users/{userId}", params);
    }
}
