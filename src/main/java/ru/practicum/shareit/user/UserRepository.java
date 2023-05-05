package ru.practicum.shareit.user;

import java.util.List;

interface UserRepository {
    List<User> findAll();

    User findById(int userId);

    User create(User newUser);

    User change(User newUser);

    void deleteUser(int userId);
}
