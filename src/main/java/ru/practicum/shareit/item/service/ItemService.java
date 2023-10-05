package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Map;

public interface ItemService {
    Item addItem(long userId, ItemDto item);
    Item updateItem(long userId, Map<String, Object> fields, long itemId);
    Item getItemById(long itemId);
    List<Item> getAllUserItems(long userId);
    List<Item> findItems(String text);
}
