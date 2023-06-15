package ru.practicum.booking;

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
public class BookingClient {

    @Value("${shareit-server.url:http://localhost:9090}")
    String serverUrl;

    private final RestTemplate rest;

    public BookingClient() {
        this.rest = new RestTemplate();
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        rest.setRequestFactory(requestFactory);
    }

    public <T> ResponseEntity<Object> findBookingById(int bookingId, Integer userId, @Nullable T body) {

        HashMap<String, Integer> params = new HashMap<>(Map.of("bookingId", bookingId));
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders(userId));
        String url = serverUrl + "/bookings/{bookingId}";

        return responseRequest(url, HttpMethod.GET, requestEntity, params);
    }

    public <T> ResponseEntity<Object> createBooking(Integer userId, @Nullable T body) {

        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders(userId));
        String url = serverUrl + "/bookings";

        return responseRequest(url, HttpMethod.POST, requestEntity, null);
    }

    public <T> Object updateBooking(Integer userId, Integer bookingId, Boolean available, @Nullable T body) {

        String url = serverUrl + "/bookings/{bookingId}?approved=" + available;
        HashMap<String, Integer> params = new HashMap<>(Map.of("bookingId", bookingId));
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders(userId));

        return responseRequest(url, HttpMethod.PATCH, requestEntity, params);
    }

    public <T> void deleteBooking(int bookingId, Integer userId, @Nullable T body) {

        HashMap<String, Integer> params = new HashMap<>(Map.of("bookingId", bookingId));
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders(userId));

        rest.delete(serverUrl + "/bookings/{bookingId}", requestEntity, params);
    }

    public <T> ResponseEntity<Object> getUsersBooking(Integer userId, String state, Integer from, Integer size, @Nullable T body) {

        String url = serverUrl + "/bookings?from=" + from + "&size=" + size + "&state=" + state;
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders(userId));

        return responseRequest(url, HttpMethod.GET, requestEntity, null);
    }

    public <T> ResponseEntity<Object> getBookingsForUsersItems(Integer userId, String state, Integer from, Integer size, @Nullable T body) {

        String url = serverUrl + "/bookings/owner?from=" + from + "&size=" + size + "&state=" + state;
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
