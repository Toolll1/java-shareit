package ru.practicum.shareit.booking;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookingRepositoryImpl implements BookingRepository {
    @Override
    public List<Booking> findAll() {

        throw new UnsupportedOperationException("method in development");
    }

    @Override
    public Booking findById(int bookingId) {

        throw new UnsupportedOperationException("method in development");
    }

    @Override
    public Booking create(Booking booking) {

        throw new UnsupportedOperationException("method in development");
    }

    @Override
    public Booking update(Booking booking) {

        throw new UnsupportedOperationException("method in development");
    }

    @Override
    public void deleteBooking(int bookingId) {

        throw new UnsupportedOperationException("method in development");
    }
}
