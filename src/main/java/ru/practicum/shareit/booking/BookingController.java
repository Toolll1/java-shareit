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

    @GetMapping
    public List<Booking> findAll() {

        return bookingService.findAll();
    }

    @GetMapping("/{bookingId}")
    public Booking findById(@PathVariable int bookingId) {

        return bookingService.findById(bookingId);
    }

    @PostMapping
    public Booking create(@Valid @RequestBody Booking booking) {

        return bookingService.create(booking);
    }

    @PutMapping
    public Booking update(@Valid @RequestBody Booking booking) {

        return bookingService.update(booking);
    }

    @DeleteMapping("/{bookingId}")
    public void deleteBooking(@PathVariable int bookingId) {

        bookingService.deleteBooking(bookingId);
    }
}
