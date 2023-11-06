package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestExtDto;

import java.util.List;

public interface RequestService {
    RequestDto createRequest(long requesterId, RequestDto request);
    List<RequestExtDto> getAllOwnRequests(long userId);
    List<RequestExtDto> getAllOtherUserRequests(long userId, int from, int size);
    RequestExtDto getRequestById(long id, long userId);
}
