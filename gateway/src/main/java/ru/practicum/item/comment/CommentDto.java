package ru.practicum.item.comment;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class CommentDto {

    private Integer id;
    @NotEmpty
    private String text;
    private Integer itemId;
    private Integer authorId;
    private String authorName;
    private LocalDateTime created;
}
