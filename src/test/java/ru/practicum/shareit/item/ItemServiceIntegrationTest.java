package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtDto;
import ru.practicum.shareit.item.mapper.MappingItem;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceIntegrationTest {
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;
    private final MappingItem mappingItem;
    private final RequestService requestService;
    private final UserDto userDto = new UserDto(
            "FirstUser",
            "FirstUser@mail.ru");
    private final UserDto userDto2 = new UserDto(
            "SecondUser",
            "SecondUser@mail.ru");
    private final ItemDto itemDto = new ItemDto(
            0L,
            "дрель",
            "с ударным режимом",
            true,
            null,
            null);
    private final ItemDto itemDto2 = new ItemDto(
            0L,
            "пила",
            "цепная",
            true,
            null,
            null);
    private final BookingDto bookingDto = new BookingDto(
            0L,
            LocalDateTime.of(2023, 10, 29, 15, 23, 34),
            LocalDateTime.of(2023, 10, 31, 16, 14, 56));
    private final RequestDto requestDto = new RequestDto(null, "нужен дрель", null);

    @Test
    void addItem_whenInvoked_thenReturnSavedInDBItem() {
        long ownerId = userService.addUser(userDto).getId();

        ItemDto savedItem = itemService.addItem(ownerId, itemDto);

        assertThat(savedItem, notNullValue());
        assertThat(savedItem.getName(), equalTo(itemDto.getName()));
        assertThat(savedItem.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(savedItem.getAvailable(), equalTo(itemDto.getAvailable()));
        assertThat(savedItem.getRequestId(), equalTo(itemDto.getRequestId()));
        assertThat(savedItem.getComments(), equalTo(itemDto.getComments()));
    }

    @Test
    void getItemById_whenInvoked_thenReturnItemFromDB() {
        long ownerId = userService.addUser(userDto).getId();
        ItemDto savedItem = itemService.addItem(ownerId, itemDto);

        ItemExtDto returnedItem = itemService.getItemById(ownerId, savedItem.getId());

        assertThat(returnedItem, notNullValue());
        assertThat(returnedItem.getId(), equalTo(savedItem.getId()));
        assertThat(returnedItem.getName(), equalTo(savedItem.getName()));
    }

    @Test
    void getAllUserItems_whenInvoked_thenReturnListOfItemsBelongUser() {
        long ownerId = userService.addUser(userDto).getId();
        ItemDto savedItem1 = itemService.addItem(ownerId, itemDto);
        ItemDto savedItem2 = itemService.addItem(ownerId, itemDto2);
        ItemExtDto returnedItem1 = itemService.getItemById(ownerId, savedItem1.getId());
        ItemExtDto returnedItem2 = itemService.getItemById(ownerId, savedItem2.getId());

        List<ItemExtDto> items = itemService.getAllUserItems(ownerId, 0, 5);

        assertThat(items, notNullValue());
        assertThat(items.size(), equalTo(2));
        assertThat(items, hasItem(returnedItem1));
        assertThat(items, hasItem(returnedItem2));
    }

    @Test
    void findItems_whenInvoked_thenReturnListOfItemContainingTextInNameOrDescription() {
        long ownerId = userService.addUser(userDto).getId();
        ItemDto savedItem1 = itemService.addItem(ownerId, itemDto);
        ItemDto savedItem2 = itemService.addItem(ownerId, itemDto2);

        List<ItemDto> items = itemService.findItems("пила", 0, 5);

        assertThat(items, notNullValue());
        assertThat(items.size(), equalTo(1));
        assertThat(items, hasItem(savedItem2));
    }

    @Test
    void findItems_whenTextIsBlank_thenReturnEmptyList() {
        long ownerId = userService.addUser(userDto).getId();
        itemService.addItem(ownerId, itemDto);

        List<ItemDto> items = itemService.findItems("", 0, 5);

        assertThat(items, notNullValue());
        assertThat(items.size(), equalTo(0));
    }

    @Test
    void updateItem_whenInvoked_thenReturnUpdatedItem() {
        long ownerId = userService.addUser(userDto).getId();
        long itemId = itemService.addItem(ownerId, itemDto).getId();
        Map<String, Object> fields = new HashMap<>();
        fields.put("name", "дрель электрическая");
        fields.put("description", "без ударного режима");
        fields.put("available", false);

        ItemDto item = itemService.updateItem(ownerId, fields, itemId);

        assertThat(item, notNullValue());
        assertThat(item.getId(), equalTo(itemId));
        assertThat(item.getName(), equalTo(fields.get("name")));
        assertThat(item.getDescription(), equalTo(fields.get("description")));
        assertThat(item.getAvailable(), equalTo(fields.get("available")));
    }

    @Test
    void addComment_whenInvoked_thenReturnSavedComment() {
        long ownerId = userService.addUser(userDto).getId();
        User booker = userService.addUser(userDto2);
        long bookerId = booker.getId();
        ItemDto item = itemService.addItem(ownerId, itemDto);
        long itemId = item.getId();
        bookingDto.setItemId(item.getId());
        long bookingId = bookingService.createBooking(bookerId, bookingDto).getId();
        bookingService.confirmBooking(ownerId, "true", bookingId);
        CommentDto commentDto = new CommentDto(
                null,
                "the text of the comment",
                mappingItem.mapToItem(item),
                booker.getName(),
                null);

        CommentDto createdComment = itemService.addComment(bookerId, itemId, commentDto);

        assertThat(createdComment, notNullValue());
        assertThat(createdComment.getItem(), notNullValue());
        assertThat(createdComment.getText(), equalTo(commentDto.getText()));
        assertThat(createdComment.getAuthorName(), equalTo(commentDto.getAuthorName()));
        assertThat(createdComment.getCreated(), notNullValue());
    }

    @Test
    void getItemByRequestId_whenInvoked_thenReturnItem() {
        long ownerId = userService.addUser(userDto).getId();
        long requesterId = userService.addUser(userDto2).getId();
        Long requestId = requestService.createRequest(requesterId, requestDto).getId();
        itemDto.setRequestId(requestId);
        ItemDto createdItem = itemService.addItem(ownerId, itemDto);

        ItemDto returnedItem = itemService.getItemByRequestId(requestId);

        assertThat(returnedItem, notNullValue());
        assertThat(returnedItem, equalTo(createdItem));
    }

    @Test
    void getAllByRequestId_whenInvoked_thenReturnListOfItem() {
        long ownerId = userService.addUser(userDto).getId();
        long requesterId = userService.addUser(userDto2).getId();
        Long requestId = requestService.createRequest(requesterId, requestDto).getId();
        itemDto.setRequestId(requestId);
        ItemDto createdItem = itemService.addItem(ownerId, itemDto);

        List<ItemDto> items = itemService.getAllByRequestIdIn(Set.of(requestId));

        assertThat(items, notNullValue());
        assertThat(items.size(), equalTo(1));
        assertThat(items, hasItem(createdItem));
    }
}