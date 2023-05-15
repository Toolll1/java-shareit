package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> findAll() {

        log.info("Received a request to search for all users");

        return userRepository.findAll();
    }

    public User findById(int userId) {

        log.info("Searching for a user with an id " + userId);

        return userRepository.findById(userId);
    }

    public User create(User user) {

        User newUser = userRepository.create(user);

        log.info("I received a request to create a user " + newUser);

        return newUser;
    }

    public User update(User user) {

        User newUser = userRepository.update(user);

        log.info("I received a request to update a user\n" + "Old user " + user + "\nNew user " + newUser);

        return newUser;
    }

    public void deleteUser(int userId) {

        userRepository.deleteUser(userId);

        log.info("I received a request to delete a user with an id " + userId);
    }
}
