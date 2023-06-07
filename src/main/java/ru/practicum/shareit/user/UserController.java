package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> findAllUsers() {

        return userService.findAllUsers();
    }

    @GetMapping("/{userId}")
    public UserDto findUserById(@PathVariable int userId) {

        return userService.findUserById(userId);
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto newUser) {

        return userService.createUser(newUser);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@RequestBody UserDto newUser, @PathVariable int userId) {

        newUser.setId(userId);
        return userService.updateUser(newUser);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable int userId) {

        userService.deleteUser(userId);
    }
}
