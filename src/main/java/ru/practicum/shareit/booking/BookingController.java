package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public BookingDto findBookingById(@RequestHeader(value = "X-Sharer-User-Id") Integer userId,
                                      @PathVariable int bookingId) {

        return bookingService.findById(bookingId, userId);
    }

    @PostMapping
    public BookingDto createBooking(@Valid @RequestBody BookingDto booking,
                                    @RequestHeader("X-Sharer-User-Id") Integer userId) {

        return bookingService.create(booking, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@RequestHeader(value = "X-Sharer-User-Id") Integer userId,
                                    @PathVariable("bookingId") Integer bookingId,
                                    @RequestParam(name = "approved") Boolean available) {

        return bookingService.update(userId, bookingId, available);
    }


    @DeleteMapping("/{bookingId}")
    public void deleteBooking(@PathVariable int bookingId,
                              @RequestHeader("X-Sharer-User-Id") Integer userId) {

        bookingService.deleteBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getBookingsForUser(@RequestHeader(value = "X-Sharer-User-Id") Integer userId,
                                               @RequestParam(name = "state", defaultValue = "ALL") String state,
                                               @RequestParam(value = "from", defaultValue = "0", required = false) Integer from,
                                               @RequestParam(value = "size", defaultValue = "10", required = false) Integer size) {

        return bookingService.getUsersBooking(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsForUsersItems(@RequestHeader(value = "X-Sharer-User-Id") Integer userId,
                                                     @RequestParam(name = "state", defaultValue = "ALL") String state,
                                                     @RequestParam(value = "from", defaultValue = "0", required = false) Integer from,
                                                     @RequestParam(value = "size", defaultValue = "10", required = false) Integer size) {

        return bookingService.getBookingsForUsersItems(userId, state, from, size);
    }
}
