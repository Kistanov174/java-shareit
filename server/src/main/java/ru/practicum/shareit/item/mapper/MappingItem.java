package ru.practicum.shareit.item.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Service
@RequiredArgsConstructor
public class MappingItem {
    public ItemDto mapToItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setRequestId(item.getRequestId());
        itemDto.setAvailable(item.getAvailable());
        return itemDto;
    }

    public ItemExtDto mapToExtItemDto(Item item) {
        ItemExtDto itemExtDto = new ItemExtDto();
        itemExtDto.setId(item.getId());
        itemExtDto.setName(item.getName());
        itemExtDto.setDescription(item.getDescription());
        itemExtDto.setAvailable(item.getAvailable());
        itemExtDto.setOwner(item.getOwner());
        itemExtDto.setRequestId(item.getRequestId());
        return itemExtDto;
    }

    public Item mapToItem(ItemDto itemDto) {
        String name = itemDto.getName();
        String description = itemDto.getDescription();
        boolean available = itemDto.getAvailable();
        Long requestId = itemDto.getRequestId();
        return new Item(null, name, description, available, null, requestId);
    }

    public Item mapToItem(ItemExtDto itemExtDto) {
        long id = itemExtDto.getId();
        String name = itemExtDto.getName();
        String description = itemExtDto.getDescription();
        boolean available = itemExtDto.getAvailable();
        User owner = itemExtDto.getOwner();
        Long requestId = itemExtDto.getRequestId();
        return new Item(id, name, description, available, owner, requestId);
    }
}