package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public BookingDto findBookingById(@RequestHeader(value = "X-Sharer-User-Id") Integer userId,
                                      @PathVariable int bookingId) {

        return bookingService.findBookingById(bookingId, userId);
    }

    @PostMapping
    public BookingDto createBooking(@RequestBody BookingDto booking,
                                    @RequestHeader("X-Sharer-User-Id") Integer userId) {

        return bookingService.createBooking(booking, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@RequestHeader(value = "X-Sharer-User-Id") Integer userId,
                                    @PathVariable("bookingId") Integer bookingId,
                                    @RequestParam(name = "approved") Boolean available) {

        return bookingService.updateBooking(userId, bookingId, available);
    }


    @DeleteMapping("/{bookingId}")
    public void deleteBooking(@PathVariable int bookingId,
                              @RequestHeader("X-Sharer-User-Id") Integer userId) {

        bookingService.deleteBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getBookingsForUser(@RequestHeader(value = "X-Sharer-User-Id") Integer userId,
                                               @RequestParam(name = "state") String state,
                                               @RequestParam(value = "from") Integer from,
                                               @RequestParam(value = "size") Integer size) {

        return bookingService.getUsersBooking(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsForUsersItems(@RequestHeader(value = "X-Sharer-User-Id") Integer userId,
                                                     @RequestParam(name = "state") String state,
                                                     @RequestParam(value = "from") Integer from,
                                                     @RequestParam(value = "size") Integer size) {

        return bookingService.getBookingsForUsersItems(userId, state, from, size);
    }
}
