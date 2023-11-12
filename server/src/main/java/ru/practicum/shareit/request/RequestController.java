package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestExtDto;
import ru.practicum.shareit.request.service.RequestService;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class RequestController {
    private final RequestService requestService;

    @PostMapping
    public RequestDto createRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @RequestBody RequestDto request) {
        return requestService.createRequest(userId, request);
    }

    @GetMapping
    public List<RequestExtDto> getAllUserRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Create request from user with id = " + userId + "to get all his requests of items");
        return requestService.getAllOwnRequests(userId);
    }

    @GetMapping("/all")
    public List<RequestExtDto> getAllOtherUserRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                                       @RequestParam(defaultValue = "0") int from,
                                                       @RequestParam(defaultValue = "10") int size) {
        return requestService.getAllOtherUserRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public RequestExtDto getRequestById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long requestId) {
        log.info("Create request to get request of item with id = " + requestId);
        return requestService.getRequestById(requestId, userId);
    }
}