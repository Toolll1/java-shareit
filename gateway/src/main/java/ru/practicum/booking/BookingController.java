package ru.practicum.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingClient bookingClient;

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findBookingById(@RequestHeader(value = "X-Sharer-User-Id") Integer userId,
                                                  @PathVariable int bookingId) {

        return bookingClient.findBookingById(bookingId, userId, null);
    }

    @PostMapping
    public ResponseEntity<Object> createBooking(@Valid @RequestBody BookingDto booking,
                                                @RequestHeader("X-Sharer-User-Id") Integer userId) {

        return bookingClient.createBooking(userId, booking);
    }

    @PatchMapping("/{bookingId}")
    public Object updateBooking(@RequestHeader(value = "X-Sharer-User-Id") Integer userId,
                                @PathVariable("bookingId") Integer bookingId,
                                @RequestParam(name = "approved") Boolean available) {

        return bookingClient.updateBooking(userId, bookingId, available, null);
    }


    @DeleteMapping("/{bookingId}")
    public void deleteBooking(@PathVariable int bookingId,
                              @RequestHeader("X-Sharer-User-Id") Integer userId) {

        bookingClient.deleteBooking(bookingId, userId, null);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsForUser(@RequestHeader(value = "X-Sharer-User-Id") Integer userId,
                                                     @RequestParam(name = "state", defaultValue = "ALL") String state,
                                                     @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                     @RequestParam(value = "size", defaultValue = "10") Integer size) {

        return bookingClient.getUsersBooking(userId, state, from, size, null);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsForUsersItems(@RequestHeader(value = "X-Sharer-User-Id") Integer userId,
                                                           @RequestParam(name = "state", defaultValue = "ALL") String state,
                                                           @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                           @RequestParam(value = "size", defaultValue = "10") Integer size) {

        return bookingClient.getBookingsForUsersItems(userId, state, from, size, null);
    }
}
