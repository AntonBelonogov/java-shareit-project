package ru.practicum.shareit.request.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestDto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ItemRequestRepository implements ItemRequestStorage{

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ItemRequestRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<ItemRequest> getItemRequest() {
        final String sqlQuery = "SELECT * FROM item_request";
        return jdbcTemplate.query(sqlQuery, this::mapRow);
    }

    @Override
    public ItemRequest getItemRequest(Long itemId) {
        return null;
    }

    @Override
    public ItemRequest addItemRequest(ItemRequest item) {
        return null;
    }

    @Override
    public ItemRequest updateItemRequest(Long itemRequestId, ItemRequest item) {
        return null;
    }

    @Override
    public Boolean deleteItemRequest(Long itemRequestId) {
        return null;
    }

    private ItemRequest mapRow(ResultSet rs, int rowNum) throws SQLException {
        return ItemRequest.builder()
                .id(rs.getLong("id"))
                .description(rs.getString("description"))
                .build();
    }
}
