package ru.practicum.shareit.request.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestExtDto;
import ru.practicum.shareit.request.mapper.MappingRequest;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final MappingRequest mappingRequest;
    private final UserService userService;
    private final ItemService itemService;
    private static final Sort SORT_BY_CREATED_DATE_DESC = Sort.by(Sort.Direction.DESC, "created");

    @Override
    public RequestDto createRequest(long requesterId, RequestDto requestDto) {
        userService.findUserById(requesterId);
        User requester = userService.findUserById(requesterId);
        Request request = mappingRequest.mapToRequest(requestDto);
        request.setRequester(requester);
        request.setCreated(LocalDateTime.now());
        return mappingRequest.mapToRequestDto(requestRepository.save(request));
    }

    @Override
    public List<RequestExtDto> getAllOwnRequests(long userId) {
        userService.findUserById(userId);
        List<Request> requests = requestRepository.findAllByRequesterId(userId, SORT_BY_CREATED_DATE_DESC);
        return addItemsIntoRequest(requests);
    }

    @Override
    public List<RequestExtDto> getAllOtherUserRequests(long userId, int from, int size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        List<Request> requests = requestRepository.findAllExcludingRequestsWithRequesterId(userId, page);
        return addItemsIntoRequest(requests);
    }

    @Override
    public RequestExtDto getRequestById(long id, long userId) {
        userService.findUserById(userId);
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Request with id = " + id + " hasn't been found"));
        RequestExtDto requestExtDto = mappingRequest.mapToRequestExtDto(request);
        List<ItemDto> items = itemService.getAllByRequestIdIn(Set.of(id));
        requestExtDto.setItems(items);
        return requestExtDto;
    }

    private RequestExtDto makeRequestWithItem(RequestExtDto request, List<ItemDto> items) {
        return new RequestExtDto(request.getId(), request.getDescription(), request.getCreated(), items);
    }

    private List<RequestExtDto> addItemsIntoRequest(List<Request> requests) {
        Map<Long, RequestExtDto> requestsWithoutItems = requests.stream()
                .map(mappingRequest::mapToRequestExtDto)
                .collect(Collectors.toMap(RequestExtDto::getId, Function.identity()));

        Map<Long, List<ItemDto>> items = itemService.getAllByRequestIdIn(requestsWithoutItems.keySet()).stream()
                .filter(item -> item.getRequestId() != null)
                .collect(Collectors.groupingBy(ItemDto::getRequestId));

        return requestsWithoutItems.values().stream()
                .map(request -> makeRequestWithItem(request, items.getOrDefault(request.getId(),
                        Collections.emptyList())))
                .collect(Collectors.toList());
    }
}