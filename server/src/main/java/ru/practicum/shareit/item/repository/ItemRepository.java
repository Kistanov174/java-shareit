package ru.practicum.shareit.item.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerId(long ownerId, Pageable page);

    @Query("select i from Item as i where ((i.available = true) and ((lower(i.name) like" +
            " lower(concat('%', ?1, '%'))) or (lower(i.description) like lower(concat('%', ?1, '%')))))")
    List<Item> findAllContainingIgnoreCase(String text, Pageable page);

    Item findByRequestId(long requestId);

    List<Item> findAllByRequestIdIn(Set<Long> requestId);
}