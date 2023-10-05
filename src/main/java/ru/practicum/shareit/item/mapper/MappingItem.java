package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Service
public class MappingItem {
    public ItemDto mapToItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        return itemDto;
    }

    public Item mapToItem(ItemDto itemDto) {
        String name = itemDto.getName();
        String description = itemDto.getDescription();
        boolean available = itemDto.getAvailable();
        return new Item(null, name, description, available, null, null);
    }
}
