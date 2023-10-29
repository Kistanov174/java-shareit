package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtDto;
import ru.practicum.shareit.item.mapper.MappingComment;
import ru.practicum.shareit.item.mapper.MappingItem;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Comparator;
import java.util.Collections;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final MappingItem mappingItem;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final MappingComment mappingComment;

    @Override
    public ItemDto addItem(long userId, ItemDto itemDto) {
        Item item = mappingItem.mapToItem(itemDto);
        item.setOwner(userService.findUserById(userId));
        return  mappingItem.mapToItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(long userId, Map<String, Object> fields, long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException("Iten with id = " + itemId + " doesn't exist"));
        if (item.getOwner().getId() != userId) {
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
        return mappingItem.mapToItemDto(itemRepository.save(item));
    }

    @Override
    public ItemExtDto getItemById(long userId, long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException("Iten with id = " + itemId + " doesn't exist"));
        ItemExtDto itemExtDto = mappingItem.mapToExtItemDto(item);
        if (userId == item.getOwner().getId()) {
            itemExtDto.setLastBooking(bookingRepository
                    .findFirstByItemIdAndStartBeforeOrderByEndDesc(itemId, LocalDateTime.now()));
            itemExtDto.setNextBooking(bookingRepository
                    .findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(itemId,
                            LocalDateTime.now(), Status.APPROVED));
        }
        itemExtDto.setComments(findAllByItemId(itemId));
        return itemExtDto;
    }

    @Override
    public List<ItemExtDto> getAllUserItems(long userId, int from, int size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        return (itemRepository.findAllByOwnerId(userId, page).stream()
                .map(this::changeItem)
                .sorted(Comparator.comparing(ItemExtDto::getId))
                .collect(Collectors.toList()));
    }

    @Override
    public List<ItemDto> findItems(String text, int from, int size) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        return itemRepository.findAllContainingIgnoreCase(text.toLowerCase(), page).stream()
                .map(mappingItem::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> findAllByItemId(long itemId) {
        return commentRepository.findAllByItemId(itemId).stream()
                .map(mappingComment::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(long userId, long itemId, CommentDto commentDto) {
        Sort sortByStartDesc = Sort.by(Sort.Direction.DESC, "start");
        PageRequest page = PageRequest.of(0, 100, sortByStartDesc);
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndEndIsBefore(userId,
                        LocalDateTime.now(), page).stream()
                .filter(bookingOutDto -> bookingOutDto.getItem()
                        .getId() == itemId && bookingOutDto.getStatus() == Status.APPROVED)
                .collect(Collectors.toList());
        if (!bookings.isEmpty()) {
            Comment comment = mappingComment.mapToItem(commentDto);
            comment.setAuthor(bookings.get(0).getBooker());
            comment.setItem(bookings.get(0).getItem());
            return mappingComment.mapToItemDto(commentRepository.save(comment));
        }
        throw new ValidationException("User with id = " + userId + " can't add comments for things with id = " + itemId);
    }

    @Override
    public ItemDto getItemByRequestId(long requestId) {
        return mappingItem.mapToItemDto(itemRepository.findByRequestId(requestId));
    }

    @Override
    public List<ItemDto> getAllByRequestIdIn(Set<Long> requestId) {
        return itemRepository.findAllByRequestIdIn(requestId).stream()
                .map(mappingItem::mapToItemDto)
                .collect(Collectors.toList());
    }

    private ItemExtDto changeItem(Item item) {
        ItemExtDto itemExtDto = mappingItem.mapToExtItemDto(item);
        itemExtDto.setLastBooking(bookingRepository
                .findFirstByItemIdAndStartBeforeOrderByEndDesc(item.getId(), LocalDateTime.now()));
        itemExtDto.setNextBooking(bookingRepository
                .findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(item.getId(),
                        LocalDateTime.now(), Status.APPROVED));
        return itemExtDto;
    }

    private void checkUser(long userId) {
        userService.findUserById(userId);
    }
}