package ru.practicum.shareit.request.repository;

import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestStorage {
    List<ItemRequest> getItemRequest();

    ItemRequest getItemRequest(Long itemId);

    ItemRequest addItemRequest(ItemRequest item);

    ItemRequest updateItemRequest(Long itemRequestId, ItemRequest item);

    Boolean deleteItemRequest(Long itemRequestId);
}
