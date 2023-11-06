package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
public class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;
    private final User user = new User(0, "user", "user@gmail.ru");
    private final User booker = new User(0, "booker", "bookerr@gmail.ru");
    private final Item item = new Item(null, "паяльник", "60 Вт 220В", true, user, null);
    private final Booking booking = new Booking(
            null,
            LocalDateTime.of(2023, 8,1, 15, 23, 34),
            LocalDateTime.of(2023, 8,3, 16, 53, 15),
            item,
            booker,
            Status.APPROVED);
    private final Comment comment = new Comment(null, "быстро нагревается, удобная рукоядка", item, booker,
            LocalDateTime.now());

    @Test
    void shouldGetListCommentForItem() {
        userRepository.save(user);
        userRepository.save(booker);
        Item saveItem = itemRepository.save(item);
        bookingRepository.save(booking);
        commentRepository.save(comment);

        List<Comment> comments = commentRepository.findAllByItemId(saveItem.getId());

        Assertions.assertNotNull(comments);
        Assertions.assertEquals(1, comments.size());
        Assertions.assertEquals(1, comments.get(0).getId());
        Assertions.assertEquals("быстро нагревается, удобная рукоядка", comments.get(0).getText());
        Assertions.assertEquals(saveItem, comments.get(0).getItem());
        Assertions.assertEquals(booker, comments.get(0).getAuthor());
    }
}