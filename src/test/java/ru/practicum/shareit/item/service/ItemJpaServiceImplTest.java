package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;
import ru.practicum.shareit.user.service.UserJpaServiceImpl;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class ItemJpaServiceImplTest {

    @Mock
    private ItemJpaRepository itemRepository;

    @Mock
    private UserJpaRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserJpaServiceImpl userJpaService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemJpaServiceImpl itemService;

    private EntityManager entityManager;

    private User user;
    private Booking booking;
    private Item item;
    private Comment comment;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        item = Item.builder()
                .id(1L)
                .name("item1")
                .description("item1")
                .owner(user)
                .request(itemRequest)
                .available(true)
                .build();

        user = User.builder()
                .id(1L)
                .name("user")
                .email("user@user.com")
                .build();

        booking = booking.builder()
                .id(1L)
                .item(item)
                .booker(user)
                .start(LocalDateTime.of(2023, Month.JULY, 1, 12, 0))
                .end(LocalDateTime.of(2023, Month.JULY, 2, 12, 0))
                .status(BookingStatus.WAITING)
                .build();

        comment = Comment.builder()
                .id(1L)
                .item(item)
                .user(user)
                .created(LocalDateTime.now())
                .build();

        itemRequest = ItemRequest.builder()
                .id(1L)
                .requestor(User.builder()
                        .id(2L)
                        .name("user2")
                        .email("user2@user.com")
                        .build())
                .description("user2")
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void getItems() {
        Booking booking1 = booking;
        booking1.setId(2L);
        when(userRepository.existsById(user.getId())).thenReturn(true);

        when(commentRepository.existsById(item.getId())).thenReturn(true);
        when(itemRepository.findAllByOwnerId(user.getId())).thenReturn(List.of(item));
        when(commentRepository.findByItem_Id(item.getId())).thenReturn(List.of(comment));
        when(bookingRepository.findAllByItemIdOrderByStartAsc(item.getId())).thenReturn(List.of(booking,booking1));

        List<ItemInfoDto> items = itemService.getItems(user.getId());

        assertNotNull(items);
        assertEquals(items.get(0).getId(), item.getId());
    }

    @Test
    void getItem() {
        ItemDto itemDto = ItemMapper.toDto(item);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        assertThrows(ObjectNotFoundException.class, () -> itemService.addItem(999L, ItemMapper.toDto(item)));
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        itemDto.setRequestId(2L);
        assertThrows(ObjectNotFoundException.class, () -> itemService.addItem(user.getId(), itemDto));

        itemDto.setRequestId(1L);
        ItemDto result = itemService.addItem(user.getId(), itemDto);
        assertNotNull(result);
    }

    @Test
    void addItem() {
        ItemDto itemDto = ItemMapper.toDto(item);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        assertThrows(ObjectNotFoundException.class, () -> itemService.addItem(999L, itemDto));
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any(Item.class))).thenReturn(ItemMapper.toItem(itemDto));

        itemDto.setRequestId(2L);
        assertThrows(ObjectNotFoundException.class, () -> itemService.addItem(user.getId(), itemDto));

        itemDto.setRequestId(1L);
        ItemDto addedItem = itemService.addItem(user.getId(), itemDto);
        assertNotNull(addedItem);
    }

    @Test
    void deleteItem() {
    }

    @Test
    void searchItems() {
    }

    @Test
    void addComment() {
    }
}