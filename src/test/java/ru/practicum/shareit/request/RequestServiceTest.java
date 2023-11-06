package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.mapper.MappingRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.request.service.impl.RequestServiceImpl;
import ru.practicum.shareit.user.service.UserService;

@ExtendWith(MockitoExtension.class)
public class RequestServiceTest {
    @Mock
    UserService userService;
    @Mock
    ItemService itemService;
    @Mock
    RequestRepository requestRepository;

    @Test
    @DisplayName("Создание запроса несуществующим пользователем")
    void shouldGetObjectNotFoundException() {
        RequestService requestService = new RequestServiceImpl(requestRepository,
                new MappingRequest(), userService, itemService);
        Mockito
                .when(userService.findUserById(Mockito.anyLong()))
                .thenThrow(new ObjectNotFoundException("Пользователь не найден"));

        final ObjectNotFoundException exception = Assertions.assertThrows(ObjectNotFoundException.class,
                () -> requestService.createRequest(1, new RequestDto(null,
                        "Нужен перфоратор", null)));

        Assertions.assertEquals("Пользователь не найден", exception.getMessage());
    }
}