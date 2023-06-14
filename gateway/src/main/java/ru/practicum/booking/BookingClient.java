package ru.practicum.booking;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
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
public class BookingClient {

    public <T> ResponseEntity<Object> findBookingById(int bookingId, Integer userId, @Nullable T body) {

        HashMap<String, Integer> params = new HashMap<>(Map.of("bookingId", bookingId));
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders(userId));
        String url = "http://localhost:9090/bookings/{bookingId}";
        HttpMethod httpMethod = HttpMethod.GET;

        return responseRequest(url, params, requestEntity, httpMethod);
    }

    public <T> ResponseEntity<Object> createBooking(Integer userId, @Nullable T body) {

        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders(userId));
        String url = "http://localhost:9090/bookings";
        HttpMethod httpMethod = HttpMethod.POST;

        return responseRequest(url, null, requestEntity, httpMethod);
    }

    public <T> Object updateBooking(Integer userId, Integer bookingId, Boolean available, @Nullable T body) {

        String url = ("http://localhost:9090/bookings/{bookingId}?approved=" + available);
        HashMap<String, Integer> params = new HashMap<>(Map.of("bookingId", bookingId));
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders(userId));
        HttpMethod httpMethod = HttpMethod.PATCH;

        return responseRequest(url, params, requestEntity, httpMethod);
    }

    public <T> void deleteBooking(int bookingId, Integer userId, @Nullable T body) {

        HashMap<String, Integer> params = new HashMap<>(Map.of("bookingId", bookingId));
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders(userId));

        new RestTemplate().delete("http://localhost:9090/bookings/{bookingId}", requestEntity, params);
    }

    public <T> ResponseEntity<Object> getUsersBooking(Integer userId, String state, Integer from, Integer size, @Nullable T body) {

        String url = ("http://localhost:9090/bookings?from=" + from + "&size=" + size + "&state=" + state);
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders(userId));
        HttpMethod httpMethod = HttpMethod.GET;

        return responseRequest(url, null, requestEntity, httpMethod);
    }

    public <T> ResponseEntity<Object> getBookingsForUsersItems(Integer userId, String state, Integer from, Integer size, @Nullable T body) {

        String url = ("http://localhost:9090/bookings/owner?from=" + from + "&size=" + size + "&state=" + state);
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders(userId));
        HttpMethod httpMethod = HttpMethod.GET;

        return responseRequest(url, null, requestEntity, httpMethod);
    }

    private RestTemplate restTemplate() {

        RestTemplate restTemplate = new RestTemplate();
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        restTemplate.setRequestFactory(requestFactory);

        return restTemplate;
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

    private <T> ResponseEntity<Object> responseRequest(String url, HashMap<String, Integer> params, HttpEntity<T> requestEntity, HttpMethod httpMethod) {

        ResponseEntity<Object> response;

        try {

            if (params != null) {
                response = restTemplate().exchange(url, httpMethod, requestEntity, Object.class, params);
            } else {
                response = restTemplate().exchange(url, httpMethod, requestEntity, Object.class);
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
