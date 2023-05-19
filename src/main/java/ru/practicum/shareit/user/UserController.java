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
    public List<UserDto> findAll() {

        return userService.findAll();
    }

    @GetMapping("/{userId}")
    public UserDto findById(@PathVariable int userId) {

        return userService.findById(userId);
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto newUser) {

        return userService.create(newUser);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@RequestBody UserDto newUser, @PathVariable int userId) {

        newUser.setId(userId);

        return userService.update(newUser);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable int userId) {

        userService.deleteUser(userId);
    }
}
