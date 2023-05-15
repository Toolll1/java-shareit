package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;

    public List<Booking> findAll() {

        return bookingRepository.findAll();
    }

    public Booking findById(int bookingId) {

        return bookingRepository.findById(bookingId);
    }

    public Booking create(Booking booking) {

        return bookingRepository.create(booking);
    }

    public Booking update(Booking booking) {

        return bookingRepository.update(booking);
    }

    public void deleteBooking(int bookingId) {

        bookingRepository.deleteBooking(bookingId);
    }
}
