package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    @Query(value = "select * from requests r where r.requester_id <> ?1 order by r.created desc", nativeQuery = true)
    List<Request> findAllExcludingRequestsWithRequesterId(long requesterId, Pageable page);

    List<Request> findAllByRequesterId(long requesterId, Sort sort);
}
