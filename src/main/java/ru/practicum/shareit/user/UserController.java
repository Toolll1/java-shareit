package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<User> findAll() {

        return userService.findAll();
    }

    @GetMapping("/{userId}")
    public User findById(@PathVariable int userId) {

        return userService.findById(userId);
    }

    @PostMapping
    public User create(@Valid @RequestBody User newUser) {

        return userService.create(newUser);
    }

    @PatchMapping("/{userId}")
    public User update(@RequestBody User newUser, @PathVariable int userId) {

        newUser.setId(userId);
        return userService.update(newUser);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable int userId) {

        userService.deleteUser(userId);
    }
}
