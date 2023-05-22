package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.exception.InvalidEntityException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.Month;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceTest {

    private final BookingService bookingService;
    private final EntityManager entityManager;

    private User user;
    private User owner;
    private Item item;
    private BookingInfoDto booking1;
    private BookingInfoDto booking2;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .name("User")
                .email("user@mail.com")
                .build();
        entityManager.persist(user);
        entityManager.flush();

        owner = User.builder()
                .name("Owner")
                .email("owner@mail.com")
                .build();
        entityManager.persist(owner);
        entityManager.flush();

        item = Item.builder()
                .owner(owner)
                .name("item")
                .description("description")
                .available(true)
                .build();
        entityManager.persist(item);
        entityManager.flush();

        booking1 = bookingService.addBooking(user.getId(), BookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.of(2023, Month.JULY, 1, 12, 0, 0))
                .end(LocalDateTime.of(2023, Month.JULY, 2, 12, 0, 0))
                .build());

        booking2 = bookingService.addBooking(user.getId(), BookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.of(2023, Month.JULY, 5, 12, 0, 0))
                .end(LocalDateTime.of(2023, Month.JULY, 6, 12, 0, 0))
                .build());
    }

    @Test
    void addBooking() {
        BookingDto emptyBookingDto = BookingDto.builder().build();
        assertThrows(InvalidEntityException.class, () -> {
            bookingService.addBooking(1L, emptyBookingDto);
        });

        assertThat(booking1.getId(), notNullValue());
        assertThat(booking1.getStart(), equalTo(LocalDateTime.of(2023, Month.JULY, 1, 12, 0, 0)));
        assertThat(booking1.getEnd(), equalTo(LocalDateTime.of(2023, Month.JULY, 2, 12, 0, 0)));
        assertThat(booking1.getStatus(), equalTo(BookingStatus.WAITING));
        assertThat(booking1.getBooker().getId(), equalTo(user.getId()));
        assertThat(booking1.getItem().getId(), equalTo(item.getId()));

        assertThrows(InvalidEntityException.class, () -> {
            item.setAvailable(false);
            bookingService.addBooking(1L, BookingDto.builder()
                    .itemId(item.getId())
                    .start(LocalDateTime.of(2023, Month.JULY, 5, 12, 0, 0))
                    .end(LocalDateTime.of(2023, Month.JULY, 6, 12, 0, 0))
                    .build());
        });

    }

    @Test
    void updateBookingStatus() {
        Booking booking = entityManager.find(Booking.class, booking1.getId());
        booking.setStatus(BookingStatus.APPROVED);
        entityManager.merge(booking);

        assertThrows(InvalidEntityException.class, () -> {
            bookingService.updateBookingStatus(owner.getId(), booking.getId(), true);
        });

        assertThrows(ObjectNotFoundException.class, () -> {
            bookingService.updateBookingStatus(user.getId(), booking.getId(), true);
        });
    }

    @Test
    void getCurrentBooking() {
        Booking booking = entityManager.find(Booking.class, booking1.getId());

        assertThrows(ObjectNotFoundException.class, () -> {
            bookingService.getCurrentBooking(user.getId(), 999L);
        });

        assertDoesNotThrow(() -> {
            bookingService.getCurrentBooking(user.getId(), booking.getId());
        });
    }
}