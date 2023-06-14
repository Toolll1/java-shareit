package ru.practicum.shareit.item;

import lombok.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.persistence.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
@Entity

@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Integer id;
    @Column(name = "name", nullable = false, length = 320)
    private String name;
    @Column(name = "description", nullable = false, length = 320)
    private String description;
    @Column(name = "available", nullable = false)
    private Boolean available;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;
    @OneToOne
    @JoinColumn(name = "request_id")
    private ItemRequest request;
}
