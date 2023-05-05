package ru.practicum.shareit.booking;

import org.springframework.stereotype.Service;

@Service
public class BookingMapper {

    public BookingDto bookingToDto(Booking booking) {

        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(booking.getItem())
                .booker(booking.getBooker())
                .bookingStatus(booking.getBookingStatus())
                .build();
    }

    public Booking dtoToBooking(BookingDto dto) {

        return Booking.builder()
                .id(dto.getId())
                .start(dto.getStart())
                .end(dto.getEnd())
                .item(dto.getItem())
                .booker(dto.getBooker())
                .bookingStatus(dto.getBookingStatus())
                .build();
    }
}
