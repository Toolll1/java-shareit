package ru.practicum.shareit.booking;

import lombok.*;

import java.time.LocalDateTime;

/**
 * id — уникальный идентификатор бронирования;
 * start — дата и время начала бронирования;
 * end — дата и время конца бронирования;
 * item — вещь, которую пользователь бронирует;
 * booker — пользователь, который осуществляет бронирование;
 * status — статус бронирования. Может принимать одно из следующих
 * значений: WAITING — новое бронирование, ожидает одобрения, APPROVED —
 * Дополнительные советы ментора 2
 * бронирование подтверждено владельцем, REJECTED — бронирование
 * отклонено владельцем, CANCELED — бронирование отменено создателем.
 */

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class Booking {

    private Integer id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Integer item;
    private Integer booker;
    private BookingStatus bookingStatus;
}
