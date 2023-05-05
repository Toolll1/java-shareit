package ru.practicum.shareit.booking;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookingRepositoryImpl implements BookingRepository {
    @Override
    public List<Booking> findAll() {
        return null;
    }

    @Override
    public Booking findById(int bookingId) {
        return null;
    }

    @Override
    public Booking create(Booking booking) {
        return null;
    }

    @Override
    public Booking put(Booking booking) {
        return null;
    }

    @Override
    public void deleteBooking(int bookingId) {

    }
}
