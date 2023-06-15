package ru.practicum.item;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@Transactional
public class ItemClient {

    @Value("${shareit-server.url:http://localhost:9090}")
    String serverUrl;

    private final RestTemplate rest;

    public ItemClient() {
        this.rest = new RestTemplate();
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        rest.setRequestFactory(requestFactory);
    }

    public <T> ResponseEntity<Object> findAllByOwnerId(Integer userId, Integer from, Integer size, @Nullable T body) {

        String url = serverUrl + "/items?from=" + from + "&size=" + size;
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders(userId));

        return responseRequest(url, HttpMethod.GET, requestEntity, null);
    }

    public <T> ResponseEntity<Object> findItemById(int itemId, Integer userId, @Nullable T body) {

        HashMap<String, Integer> params = new HashMap<>(Map.of("itemId", itemId));
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders(userId));
        String url = serverUrl + "/items/{itemId}";

        return responseRequest(url, HttpMethod.GET, requestEntity, params);
    }

    public <T> ResponseEntity<Object> createItem(Integer userId, @Nullable T body) {

        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders(userId));
        String url = serverUrl + "/items";

        return responseRequest(url, HttpMethod.POST, requestEntity, null);
    }

    public <T> ResponseEntity<Object> createComment(Integer userId, int itemId, @Nullable T body) {

        HashMap<String, Integer> params = new HashMap<>(Map.of("itemId", itemId));
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders(userId));
        String url = serverUrl + "/items/{itemId}/comment";

        return responseRequest(url, HttpMethod.POST, requestEntity, params);
    }

    public <T> Object updateItem(Integer userId, @Nullable T body, int itemId) {

        HashMap<String, Integer> params = new HashMap<>(Map.of("itemId", itemId));
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders(userId));
        String url = serverUrl + "/items/{itemId}";

        return responseRequest(url, HttpMethod.PATCH, requestEntity, params);
    }

    public <T> void deleteItem(int itemId, Integer userId, @Nullable T body) {

        HashMap<String, Integer> params = new HashMap<>(Map.of("itemId", itemId));
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders(userId));

        rest.delete(serverUrl + "/items/{itemId}", requestEntity, params);
    }

    public <T> ResponseEntity<Object> searchItems(String text, Integer from, Integer size, Integer userId, @Nullable T body) {

        String url = serverUrl + "/items/search?text=" + text + "&from=" + from + "&size=" + size;
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders(userId));

        return responseRequest(url, HttpMethod.GET, requestEntity, null);
    }

    private HttpHeaders defaultHeaders(Integer userId) {

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        if (userId != null) {
            headers.set("X-Sharer-User-Id", String.valueOf(userId));
        }

        return headers;
    }

    private <T> ResponseEntity<Object> responseRequest(String url, HttpMethod httpMethod, HttpEntity<T> requestEntity, HashMap<String, Integer> params) {

        ResponseEntity<Object> response;

        try {
            if (params != null) {
                response = rest.exchange(url, httpMethod, requestEntity, Object.class, params);
            } else {
                response = rest.exchange(url, httpMethod, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }
}
