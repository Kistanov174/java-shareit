package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import javax.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
public class CommentDto {
    private Long id;
    @NotBlank
    private String text;
    @JsonIgnore
    private Item item;
    private String authorName;
    private String created;
}
