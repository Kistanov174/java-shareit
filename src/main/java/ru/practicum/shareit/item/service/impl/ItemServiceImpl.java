package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;
import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final MappingItem mappingItem;
    private final UserService userService;
    private final Validator validator;

    @Override
    public Item addItem(long userId, @Valid ItemDto itemDto) {
        checkUser(userId);
        validateItem(itemDto);
        Item item = mappingItem.mapToItem(itemDto);
        item.setOwnerId(userId);
        itemRepository.addItem(item);
        return item;
    }

    @Override
    public Item updateItem(long userId, Map<String, Object> fields, long itemId) {
        Item item = itemRepository.getItemById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException("Iten with id = " + itemId + " doesn't exist"));
        if (item.getOwnerId() != userId) {
            log.info("User with id = " + userId + " isn't owner of item with id = " + itemId);
            throw new ObjectNotFoundException("User with id = " + userId + " isn't owner");
        }
        for (String field : fields.keySet()) {
            if(field.equals("name")) {
                item.setName(fields.get(field).toString());
            }
            if(field.equals("description")) {
                item.setDescription(fields.get(field).toString());
            }
            if(field.equals("available")) {
                item.setAvailable((boolean)fields.get(field));
            }
        }
        itemRepository.updateItem(itemId, item);
        return item;
    }

    @Override
    public Item getItemById(long itemId) {
        return itemRepository.getItemById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException("Iten with id = " + itemId + " doesn't exist"));
    }

    @Override
    public List<Item> getAllUserItems(long userId) {
        return new ArrayList<>(itemRepository.getAllUserItems(userId).orElseGet(ArrayList::new));
    }

    @Override
    public List<Item> findItems(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return new ArrayList<>(itemRepository.findItems(text.toLowerCase()).orElseGet(ArrayList::new));
    }

    private void checkUser(long userId) {
        userService.findUserById(userId);
        log.info("User with id = " + userId + " doesn't exist");
    }

    private void validateItem(ItemDto itemDto) {
        Set<ConstraintViolation<ItemDto>> constraintViolationSet = validator.validate(itemDto);
        if (!CollectionUtils.isEmpty(constraintViolationSet)) {
            log.debug("Validation failed - " + constraintViolationSet);
            throw new ValidationException("Validation failed " + constraintViolationSet);
        }
    }
}