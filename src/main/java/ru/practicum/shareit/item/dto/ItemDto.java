package ru.practicum.shareit.item.dto;

import lombok.Data;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import ru.practicum.shareit.item.model.Comment;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class ItemDto {
    private long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;
    private List<Comment> comments;
}
