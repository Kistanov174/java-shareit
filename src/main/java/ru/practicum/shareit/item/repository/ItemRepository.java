package ru.practicum.shareit.item.repository;

import java.util.List;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerId(long ownerId);
    @Query("select i from Item as i where ((i.available = true) and ((lower(i.name) like" +
            " lower(concat('%', ?1, '%'))) or (lower(i.description) like lower(concat('%', ?1, '%')))))")
    List<Item> findAllContainingIgnoreCase(String text);
}