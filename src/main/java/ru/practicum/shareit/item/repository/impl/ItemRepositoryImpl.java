package ru.practicum.shareit.item.repository.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> items;
    private long counterId;

    @Override
    public void addItem(Item item) {
        long id = ++counterId;
        item.setId(id);
        items.put(id, item);
        log.info("Item with id = " + item.getId() + " has added");
    }

    @Override
    public void updateItem(long itemId, Item item) {
        items.replace(item.getId(), item);
        log.info("Item with id = " + itemId + " has updated");
    }

    @Override
    public Optional<Item> getItemById(long itemId) {
        if (items.containsKey(itemId)) {
            log.info("Item with id = " + itemId + " has found");
            return Optional.of(items.get(itemId));
        }
        log.info("Item with id = " + itemId + " hasn't found");
        return Optional.empty();
    }

    @Override
    public Optional<List<Item>> getAllUserItems(long userId) {
        List<Item> userItems = items.values().stream()
                .filter(item -> item.getOwnerId().equals(userId))
                .collect(Collectors.toList());
        log.info("List of items from " + userItems.size() + " elements has found");
        return Optional.of(userItems);
    }

    @Override
    public Optional<List<Item>> findItems(String text) {
        List<Item> similarItems = items.values().stream()
                .filter(item -> item.getAvailable().equals(true))
                .filter(item -> (item.getName() + " " + item.getDescription()).toLowerCase().contains(text))
                .collect(Collectors.toList());
        log.info("List of similar items from " + similarItems.size() + " elements has found");
        return Optional.of(similarItems);
    }
}