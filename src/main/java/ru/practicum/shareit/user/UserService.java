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

       return userRepository.findAll();
    }

    public User findById(int userId) {

        return userRepository.findById(userId);
    }

    public User create(User newUser) {

        return userRepository.create(newUser);
    }

    public User change(User newUser) {

        return userRepository.change(newUser);
    }

    public void deleteUser(int userId) {

        userRepository.deleteUser(userId);
    }
}
