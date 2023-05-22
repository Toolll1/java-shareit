package ru.practicum.shareit.user;

import java.util.List;

interface OldUserRepository {

    List<User> findAll();

    User findById(int userId);

    User create(User newUser);

    User update(User newUser);

    void deleteUser(int userId);
}
