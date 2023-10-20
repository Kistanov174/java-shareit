package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.service.RequestService;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class RequestController {
    RequestService requestService;
}