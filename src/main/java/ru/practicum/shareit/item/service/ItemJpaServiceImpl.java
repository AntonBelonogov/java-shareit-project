package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.InvalidEntityException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.item.validator.ItemValidator;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ItemJpaServiceImpl implements ItemService {

    private final ItemJpaRepository repository;
    private final UserJpaRepository userRepository;

    @Autowired
    public ItemJpaServiceImpl(ItemJpaRepository repository, UserJpaRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Override
    public List<ItemDto> getItems(Long userId) {
        return repository.findAllByOwnerId(userId)
                .stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItem(Long itemId) {
        return ItemMapper.toDto(repository
                .findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException("Item not found.")));
    }

    @Override
    public ItemDto addItem(Long userId, ItemDto item) {
        if (ItemValidator.itemCheck(item)) {
            throw new InvalidEntityException("Invalid item body.");
        }
        Item newItem = ItemMapper.toItem(item);
        newItem.setOwner(userRepository.findById(userId).orElseThrow(() ->
                new ObjectNotFoundException("User not found.")));
        return ItemMapper.toDto(repository.save(newItem));
    }

    @Transactional
    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto item) {
        Item updatedItem = repository.findById(itemId)
                .orElseThrow(() ->
                        new ObjectNotFoundException("Item not found."));
        if (!Objects.equals(updatedItem.getOwner().getId(), userId)) {
            throw new ObjectNotFoundException("Item not belongs to this user.");
        }

        updatedItem = itemUpdate(updatedItem, item);
        return ItemMapper.toDto(repository.save(updatedItem));
    }

    @Transactional
    @Override
    public void deleteItem(Long itemId) {
        if (!repository.existsById(itemId)) {
            throw new ObjectNotFoundException("Item not found.");
        }
        repository.deleteById(itemId);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        } else {
            return repository.search(text).stream()
                    .map(ItemMapper::toDto)
                    .collect(Collectors.toList());
        }
    }

    private Item itemUpdate(Item updatedItem, ItemDto itemDto) {
        if (itemDto.getName() != null) {
            updatedItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            updatedItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            updatedItem.setAvailable(itemDto.getAvailable());
        }
        if (itemDto.getOwner() != null) {
            updatedItem.setOwner(userRepository.findById(itemDto.getOwner())
                    .orElseThrow(() -> new ObjectNotFoundException("User not found.")));
        }
        if (itemDto.getRequest() != null) {
            updatedItem.setRequest(new ItemRequest());
        }
        return updatedItem;
    }
}
