package ru.practicum.shareit.request;

import lombok.*;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
@Entity
@Table(name = "requests")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Integer id;
    @Column(name = "description", nullable = false, length = 320)
    private String description;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User requestor;
    @Column(name = "created_date")
    private LocalDateTime created;
}
