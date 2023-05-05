package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.ValidateException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserRepositoryImpl implements UserRepository {
    Map<Integer, User> users = new HashMap<>();
    private Integer id = 1;

    @Override
    public List<User> findAll() {

        return new ArrayList<>(users.values());
    }

    @Override
    public User findById(int userId) {

        return users.get(userId);
    }

    @Override
    public User create(User newUser) {

        for (User user : users.values()) {
            if (user.getEmail().equals(newUser.getEmail())) {
                throw new ValidateException("A user with this email already exists");
            }
        }

        newUser.setId(id++);
        users.put(newUser.getId(), newUser);

        return newUser;
    }

    @Override
    public User change(User newUser) {

        for (User user : users.values()) {
            if (user.getEmail().equals(newUser.getEmail()) && !user.getId().equals(newUser.getId())) {
                throw new ValidateException("A user with this email already exists");
            }
        }

        User user = users.get(newUser.getId());

        if (newUser.getName() != null) {
            user.setName(newUser.getName());
        }
        if (newUser.getEmail() != null) {
            user.setEmail(newUser.getEmail());
        }

        return user;
    }

    @Override
    public void deleteUser(int userId) {

        users.remove(userId);
    }
}
