package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.InvalidEntityException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.validator.ItemValidator;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private static final String ITEM_NOT_FOUND = "Item not found.";
    private static final String USER_NOT_FOUND = "User not found.";
    private final ItemRepository itemRepository;
    private final UserRepository userStorage;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userStorage) {
        this.itemRepository = itemRepository;
        this.userStorage = userStorage;
    }

    public List<ItemInfoDto> getItems(Long userId) {
        return itemRepository.getItems(userId)
                .stream()
                .map(ItemMapper::toItemInfo)
                .collect(Collectors.toList());
    }

    public ItemInfoDto getItem(Long itemId, Long userId) {
        if (!itemRepository.isItemExists(itemId)) {
            throw new ObjectNotFoundException(ITEM_NOT_FOUND);
        }
        return ItemMapper.toItemInfo(itemRepository.getItem(itemId));
    }

    public ItemDto addItem(Long userId, ItemDto item) {
        if (!userStorage.isUserExistsById(userId)) {
            throw new ObjectNotFoundException(USER_NOT_FOUND);
        }
        if (ItemValidator.itemCheck(item)) {
            throw new InvalidEntityException("Invalid item body.");
        }
        return ItemMapper.toDto(itemRepository.addItem(userId, item));
    }

    public ItemDto updateItem(Long userId, Long itemId, ItemDto item) {
        if (!itemRepository.isItemExists(itemId)) {
            throw new ObjectNotFoundException(ITEM_NOT_FOUND);
        }
        return ItemMapper.toDto(itemRepository.updateItem(userId, itemId, item));
    }

    public void deleteItem(Long itemId) {
        if (!itemRepository.isItemExists(itemId)) {
            throw new ObjectNotFoundException(ITEM_NOT_FOUND);
        }
        itemRepository.deleteItem(itemId);
    }

    public List<ItemDto> searchItems(String text) {
        return itemRepository.searchItems(text)
                .stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }
}
