package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    List<Item> getItems(Long userId);

    Item getItem(Long itemId);

    Item addItem(Long userId, ItemDto item);

    Item updateItem(Long userId, Long itemId, ItemDto item);

    Boolean deleteItem(Long itemId);

    Boolean isItemExists(Long id);

    List<Item> searchItems(String text);
}
