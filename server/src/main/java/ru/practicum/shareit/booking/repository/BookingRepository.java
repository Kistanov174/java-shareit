package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerId(long ownerId, Pageable page);

    List<Booking> findAllByBookerIdAndStartIsAfter(long ownerId, LocalDateTime start, Pageable page);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfter(long ownerId, LocalDateTime start,
                                                                 LocalDateTime end, Pageable page);

    List<Booking> findAllByBookerIdAndEndIsBefore(long ownerId, LocalDateTime end, Pageable page);

    List<Booking> findAllByBookerIdAndStatus(long ownerId, Status status, Pageable page);

    List<Booking> findAllByItemOwnerId(long ownerId, Pageable page);

    List<Booking> findAllByItemOwnerIdAndStartIsAfter(long ownerId, LocalDateTime start, Pageable page);

    List<Booking> findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(long ownerId, LocalDateTime start,
                                                                    LocalDateTime end, Pageable page);

    List<Booking> findAllByItemOwnerIdAndEndIsBefore(long ownerId, LocalDateTime end, Pageable page);

    List<Booking> findAllByItemOwnerIdAndStatus(long ownerId, Status status, Pageable page);

    BookingShortDto findFirstByItemIdAndStartBeforeOrderByEndDesc(long itemId, LocalDateTime start);

    BookingShortDto findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(long itemId,

                                                                           LocalDateTime start, Status status);

    @Query(value = "select * from booking where ((item_id = ?1)" +
            " and ((start_date <= ?2 and end_date >= ?2) or (start_date <= ?3 and end_date >= ?3)))", nativeQuery = true)
    List<Booking> findAllWithCrossingTime(long itemId, LocalDateTime start, LocalDateTime end);
}