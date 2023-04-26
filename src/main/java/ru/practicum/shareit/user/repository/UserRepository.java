package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    List<User> getUsers();

    User getUser(Long userId);

    User addUser(User user);

    User updateUser(Long userId, User user);

    Boolean deleteUser(Long userId);

    Boolean isUserExistsById(Long id);
}
