package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
public class RequestRepositoryTest {
    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private UserRepository userRepository;
    private final User user1 = new User(0, "user1", "user1@mail.ru");
    private final User user2 = new User(0, "user2", "user2@mail.ru");
    private final Request request1 = new Request(null, "нужен дрель", user2,
            LocalDateTime.of(2023, 9, 16, 14, 27, 56));
    private final Request request2 = new Request(null, "нужен пылесос", user1,
            LocalDateTime.of(2023, 10, 21, 19, 47, 12));
    private final PageRequest page = PageRequest.of(0, 5);

    @Test
    public void shouldGetListRequestsNotFromUser1() {
        userRepository.save(user1);
        userRepository.save(user2);
        requestRepository.save(request1);
        requestRepository.save(request2);
        List<Request> requests = requestRepository.findAllExcludingRequestsWithRequesterId(1, page);
        Assertions.assertNotNull(requests);
        Assertions.assertEquals(1, requests.size());
        Assertions.assertEquals(1, requests.get(0).getId());
        Assertions.assertEquals("нужен дрель", requests.get(0).getDescription());
        Assertions.assertEquals(user2, requests.get(0).getRequester());
        Assertions.assertEquals(LocalDateTime.of(2023, 9, 16, 14, 27, 56),
                requests.get(0).getCreated());
    }
}