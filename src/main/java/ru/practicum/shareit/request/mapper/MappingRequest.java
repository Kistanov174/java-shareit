package ru.practicum.shareit.request.mapper;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestExtDto;
import ru.practicum.shareit.request.model.Request;
import java.time.LocalDateTime;
import java.util.Collections;

@Service
public class MappingRequest {
    public Request mapToRequest(RequestDto requestDto) {
        String description = requestDto.getDescription();
        return new Request(null, description, null, null);
    }

    public RequestDto mapToRequestDto(Request request) {
        Long id = request.getId();
        String description = request.getDescription();
        LocalDateTime created = request.getCreated();
        return new RequestDto(id, description, created);
    }

    public RequestExtDto mapToRequestExtDto(Request request) {
        Long id = request.getId();
        String description = request.getDescription();
        LocalDateTime created = request.getCreated();
        return new RequestExtDto(id, description, created, Collections.emptyList());
    }
}
