package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtDto;
import java.util.List;
import java.util.Map;

public interface ItemService {
    ItemDto addItem(long userId, ItemDto item);

    ItemDto updateItem(long userId, Map<String, Object> fields, long itemId);

    ItemExtDto getItemById(long userId, long itemId);

    List<ItemExtDto> getAllUserItems(long userId, int from, int size);

    List<ItemDto> findItems(String text, int from, int size);

    List<CommentDto> findAllByItemId(long itemId);

    CommentDto addComment(long userId, long itemId, CommentDto comment);

    ItemDto getItemByRequestId(long requestId);
}


