package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserDto> findAllUsers() {

        log.info("Received a request to search for all users");

        return userRepository.findAll().stream().map(UserMapper::objectToDto).collect(Collectors.toList());
    }

    public UserDto findUserById(int userId) {

        log.info("Searching for a user with an id " + userId);

        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new ObjectNotFoundException("There is no user with this id");
        }

        return UserMapper.objectToDto(user.get());
    }

    public UserDto createUser(UserDto user) {

        User newUser = userRepository.save(userMapper.dtoToObject(user));

        log.info("I received a request to create a user " + newUser);

        return UserMapper.objectToDto(newUser);
    }

    public UserDto updateUser(UserDto user) {

        User oldUser = userMapper.dtoToObject(findUserById(user.getId()));

        if (user.getName() != null) {
            oldUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            oldUser.setEmail(user.getEmail());
        }

        User newUser = userRepository.save(oldUser);

        log.info("I received a request to update a user\n" + newUser);

        return UserMapper.objectToDto(newUser);
    }

    public void deleteUser(int userId) {

        userRepository.deleteById(userId);

        log.info("I received a request to delete a user with an id " + userId);
    }
}
