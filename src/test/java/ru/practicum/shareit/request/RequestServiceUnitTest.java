package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.mapper.MappingRequest;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.impl.RequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
public class RequestServiceUnitTest {
    @Mock
    RequestRepository requestRepository;
    @Mock
    MappingRequest mappingRequest;
    @Mock
    UserService userService;
    @Mock
    ItemService itemService;
    @InjectMocks
    RequestServiceImpl requestService;

    private final User requester = new User(1, "user", "user@gmail.ru");
    private final RequestDto requestDto = new RequestDto(null, "нужен дрель",
            LocalDateTime.of(2023, 9, 16, 14, 27, 56));
    private final Request request = new Request(1L, "нужен дрель", requester,
            LocalDateTime.of(2023, 9, 16, 14, 27, 56));
    private final static Sort SORT_BY_CREATED_DATE_DESC = Sort.by(Sort.Direction.DESC, "created");
    private final PageRequest page = PageRequest.of(0, 5);

    @Test
    void testVerifyCreationRequest() {
        Mockito
                .when(userService.findUserById(anyLong()))
                .thenReturn(requester);
        Mockito
                .when(mappingRequest.mapToRequest(any()))
                .thenReturn(request);

        requestService.createRequest(1, requestDto);

        Mockito.verify(requestRepository, times(1))
                .save(request);
        Mockito.verifyNoMoreInteractions(userService, requestRepository);
    }

    @Test
    void testVerifyGettingAllOwnRequests() {
        Mockito
                .when(userService.findUserById(anyLong()))
                .thenReturn(requester);

        requestService.getAllOwnRequests(requester.getId());

        Mockito.verify(requestRepository, times(1))
                .findAllByRequesterId(requester.getId(), SORT_BY_CREATED_DATE_DESC);
        Mockito.verifyNoMoreInteractions(userService, requestRepository);
    }

    @Test
    void testVerifyGettingAllOtherUserRequests() {
        requestService.getAllOtherUserRequests(requester.getId(), 0, 5);

        Mockito.verify(requestRepository, times(1))
                .findAllExcludingRequestsWithRequesterId(requester.getId(), page);
        Mockito.verifyNoMoreInteractions(requestRepository);
    }
}