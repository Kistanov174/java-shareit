package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
public class Item {
    private Long id;
    @NotEmpty
    private String name;
    @NotEmpty
    private String description;
    @NotEmpty
    private Boolean available;
    @NotBlank
    private Long ownerId;
}
