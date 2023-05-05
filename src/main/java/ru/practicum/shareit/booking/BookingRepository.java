package ru.practicum.shareit.booking;

import java.util.List;

interface BookingRepository {
    List<Booking> findAll();

    Booking findById(int bookingId);

    Booking create(Booking booking);

    Booking put(Booking booking);

    void deleteBooking(int bookingId);
}
