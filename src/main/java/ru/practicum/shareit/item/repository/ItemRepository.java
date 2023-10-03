package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;
import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    void addItem (Item item);
    void updateItem(long itemId, Item item);
    Optional<Item> getItemById(long itemId);
    Optional<List<Item>> getAllUserItems(long userId);
    Optional<List<Item>> findItems(String text);
}
