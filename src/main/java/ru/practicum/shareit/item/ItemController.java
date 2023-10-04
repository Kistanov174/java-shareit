package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public Item addItem(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody @Valid ItemDto itemDto) {
        log.info("Create request to add item: " + itemDto);
        return itemService.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody Map<String, Object> fields,
                           @PathVariable long itemId) {
        log.info("Create request to update item with id = " + itemId);
        return itemService.updateItem(userId, fields, itemId);
    }

    @GetMapping("/{itemId}")
    public Item getItemById(@PathVariable long itemId) {
        log.info("Create request to find item with id = " + itemId);
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<Item> getAllUserItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Create request to find all items of user with id = " + userId);
        return itemService.getAllUserItems(userId);
    }
    @GetMapping("/search")
    public List<Item> findItems(@RequestParam String text) {
        log.info("Create request to find all items is similar in " + text.toLowerCase());
        return itemService.findItems(text);
    }
}