package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exceptions.ObjectNotFoundException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserControllerTests {

    private final UserController userController;
    UserDto userDto = UserDto.builder().name("user").email("user@user.com").build();

    @DirtiesContext
    @Test
    public void create_returnsTheCorrectUserDto_underNormalConditions() {

        UserDto userDto1 = userController.createUser(userDto);

        assertEquals(userDto1.getId(), 1);
        assertEquals(userDto1.getName(), userDto.getName());
        assertEquals(userDto1.getEmail(), userDto.getEmail());
    }

    @DirtiesContext
    @Test
    public void create_returnsTheCorrectUserDto_withDuplicateEmail() {

        userController.createUser(userDto);

        assertThrows(DataIntegrityViolationException.class, () -> userController.createUser(UserDto.builder().name("user1").email("user@user.com").build()));
    }

    @DirtiesContext
    @Test
    public void findById_returnsTheCorrectUserDto_underNormalConditions() {

        UserDto userDto1 = userController.createUser(userDto);
        UserDto userDto2 = userController.findUserById(1);

        assertEquals(userDto1.getId(), userDto2.getId());
        assertEquals(userDto1.getName(), userDto2.getName());
        assertEquals(userDto1.getEmail(), userDto2.getEmail());
    }

    @Test
    public void findById_returnException_invalidId() {

        assertThrows(ObjectNotFoundException.class, () -> userController.findUserById(999));
    }

    @Test
    public void findAll_returnsEmptyList_inTheAbsenceOfObjects() {

        assertEquals(userController.findAllUsers().size(), 0);
    }

    @DirtiesContext
    @Test
    public void findAll_returnsTheCorrectList_underNormalConditions() {

        UserDto userDto1 = userController.createUser(userDto);
        userController.createUser(UserDto.builder().name("user1").email("user1@user.com").build());
        List<UserDto> userDtoList = userController.findAllUsers();

        assertEquals(userDtoList.size(), 2);
        assertEquals(userDtoList.get(0).getId(), userDto1.getId());
        assertEquals(userDtoList.get(0).getName(), userDto1.getName());
        assertEquals(userDtoList.get(0).getEmail(), userDto1.getEmail());
    }

    @DirtiesContext
    @Test
    public void update_returnsTheCorrectUserDto_underNormalConditions() {

        userController.createUser(userDto);
        UserDto newUserDto1 = userController.updateUser(
                UserDto.builder().name("UserUserivi4").email("UserUserivi4@user.com").build(), 1);

        assertEquals(newUserDto1.getId(), 1);
        assertEquals(newUserDto1.getName(), "UserUserivi4");
        assertEquals(newUserDto1.getEmail(), "UserUserivi4@user.com");
    }

    @DirtiesContext
    @Test
    public void update_returnsTheCorrectUserDto_withDuplicateEmail() {

        userController.createUser(userDto);
        userController.createUser(UserDto.builder().name("UserUserivi4").email("UserUserivi4@user.com").build());

        assertThrows(DataIntegrityViolationException.class, () -> userController.updateUser(
                UserDto.builder().email("UserUserivi4@user.com").build(), 1));
    }

    @DirtiesContext
    @Test
    public void delete_returnsNothing_underNormalConditions() {

        userController.createUser(userDto);
        userController.deleteUser(1);

        assertEquals(userController.findAllUsers().size(), 0);
    }

    @Test
    public void delete_returnsNothing_inTheAbsenceOfObjects() {

        assertThrows(EmptyResultDataAccessException.class, () -> userController.deleteUser(1), "There is no object with this idy");

        assertEquals(userController.findAllUsers().size(), 0);
    }
}
