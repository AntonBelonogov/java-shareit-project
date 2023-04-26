package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.EntityAlreadyExist;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepositoryImpl;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private static final String OBJECT_NOT_FOUND = "User not found.";
    private final UserRepositoryImpl userRepositoryImpl;

    @Autowired
    public UserServiceImpl(UserRepositoryImpl userRepositoryImpl) {
        this.userRepositoryImpl = userRepositoryImpl;
    }

    public List<UserDto> getUsers() {
        return userRepositoryImpl.getUsers()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public UserDto getUser(Long userId) {
        if (!userRepositoryImpl.isUserExistsById(userId)) {
            throw new ObjectNotFoundException(OBJECT_NOT_FOUND);
        }
        return UserMapper.toUserDto(userRepositoryImpl.getUser(userId));
    }

    @Override
    public UserDto addUser(UserDto user) {
        if (user.getEmail() == null) {
            throw new IllegalArgumentException("Invalid user body.");
        }
        if (userRepositoryImpl.isUserExistsByEmail(user.getEmail())) {
            throw new EntityAlreadyExist("User already exist.");
        }
        return UserMapper.toUserDto(userRepositoryImpl.addUser(UserMapper.toUser(user)));
    }

    @Override
    public UserDto updateUser(Long userId, UserDto user) {
        if (!userRepositoryImpl.isUserExistsById(userId)) {
            throw new ObjectNotFoundException(OBJECT_NOT_FOUND);
        }
        return UserMapper.toUserDto(userRepositoryImpl.updateUser(userId, UserMapper.toUser(user)));
    }

    public void deleteUser(Long userId) {
        if (!userRepositoryImpl.isUserExistsById(userId)) {
            throw new ObjectNotFoundException(OBJECT_NOT_FOUND);
        }
        userRepositoryImpl.deleteUser(userId);
    }
}
