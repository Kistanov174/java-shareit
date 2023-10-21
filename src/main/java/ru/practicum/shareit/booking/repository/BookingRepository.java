package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerId(long ownerId, Sort sort);

    List<Booking> findAllByBookerIdAndStartIsAfter(long ownerId, LocalDateTime start, Sort sort);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfter(long ownerId, LocalDateTime start,
                                                                 LocalDateTime end, Sort sort);

    List<Booking> findAllByBookerIdAndEndIsBefore(long ownerId, LocalDateTime end, Sort sort);

    List<Booking> findAllByBookerIdAndStatus(long ownerId, Status status, Sort sort);

    List<Booking> findAllByItemOwnerId(long ownerId, Sort sort);

    List<Booking> findAllByItemOwnerIdAndStartIsAfter(long ownerId, LocalDateTime start, Sort sort);

    List<Booking> findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(long ownerId, LocalDateTime start,
                                                                    LocalDateTime end, Sort sort);

    List<Booking> findAllByItemOwnerIdAndEndIsBefore(long ownerId, LocalDateTime end, Sort sort);

    List<Booking> findAllByItemOwnerIdAndStatus(long ownerId, Status status, Sort sort);

    BookingShortDto findFirstByItemIdAndStartBeforeOrderByEndDesc(long itemId, LocalDateTime start);

    BookingShortDto findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(long itemId,
                                                                           LocalDateTime start, Status status);
}