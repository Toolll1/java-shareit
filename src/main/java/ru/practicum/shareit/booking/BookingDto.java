package ru.practicum.shareit.booking;

import lombok.*;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class BookingDto {

    private Integer id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Integer itemId;
    private Integer bookerId;
    private BookingStatus bookingStatus;
}
