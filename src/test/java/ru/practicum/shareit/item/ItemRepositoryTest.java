package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import java.util.List;

@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    private final User user = new User(0, "user", "user@gmail.ru");
    private final PageRequest page = PageRequest.of(0, 5);

    @Test
    void shouldGetListItemsContainingTextInNameOrDescription() {
        User owner = userRepository.save(user);
        Item item = new Item(null, "паяльник", "паяльник электрический",
                true, owner, null);
        Item savedItem = itemRepository.save(item);
        List<Item> items = itemRepository.findAllContainingIgnoreCase("паяльник", page);
        Assertions.assertNotNull(items);
        Assertions.assertNotNull(items.get(0));
        Assertions.assertEquals(savedItem.getId(), items.get(0).getId());
        Assertions.assertEquals("паяльник", items.get(0).getName());
        Assertions.assertEquals("паяльник электрический", items.get(0).getDescription());
        Assertions.assertEquals(user, items.get(0).getOwner());
        Assertions.assertEquals(true, items.get(0).getAvailable());
    }

    @AfterEach
    void afterEach() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }
}
