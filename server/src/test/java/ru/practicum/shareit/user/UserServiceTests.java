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
public class UserServiceTests {

    private final UserService userService;
    UserDto userDto = UserDto.builder().name("user").email("user@user.com").build();

    @DirtiesContext
    @Test
    public void create_returnsTheCorrectUserDto_underNormalConditions() {

        //when
        UserDto userDto1 = userService.createUser(userDto);

        //then
        assertEquals(userDto1.getId(), 1);
        assertEquals(userDto1.getName(), userDto.getName());
        assertEquals(userDto1.getEmail(), userDto.getEmail());
    }

    @DirtiesContext
    @Test
    public void create_returnsTheCorrectUserDto_withDuplicateEmail() {

        //given
        userService.createUser(userDto);

        //then
        assertThrows(DataIntegrityViolationException.class, () -> userService.createUser(UserDto.builder().name("user1").email("user@user.com").build()));
    }

    @DirtiesContext
    @Test
    public void findById_returnsTheCorrectUserDto_underNormalConditions() {

        //given
        UserDto userDto1 = userService.createUser(userDto);

        //when
        UserDto userDto2 = userService.findUserById(1);

        //then
        assertEquals(userDto2.getId(), userDto1.getId());
        assertEquals(userDto2.getName(), userDto1.getName());
        assertEquals(userDto2.getEmail(), userDto1.getEmail());
    }

    @DirtiesContext
    @Test
    public void findById_returnException_invalidId() {

        //then
        assertThrows(ObjectNotFoundException.class, () -> userService.findUserById(999));
    }

    @DirtiesContext
    @Test
    public void findAll_returnsEmptyList_inTheAbsenceOfObjects() {

        //when
        List<UserDto> userDtoList = userService.findAllUsers();

        //then
        assertEquals(userDtoList.size(), 0);
    }

    @DirtiesContext
    @Test
    public void findAll_returnsTheCorrectList_underNormalConditions() {

        //given
        UserDto userDto1 = userService.createUser(userDto);
        userService.createUser(UserDto.builder().name("user1").email("user1@user.com").build());

        //when
        List<UserDto> userDtoList = userService.findAllUsers();

        //then
        assertEquals(userDtoList.size(), 2);
        assertEquals(userDtoList.get(0).getId(), userDto1.getId());
        assertEquals(userDtoList.get(0).getName(), userDto1.getName());
        assertEquals(userDtoList.get(0).getEmail(), userDto1.getEmail());
    }

    @DirtiesContext
    @Test
    public void update_returnsTheCorrectUserDto_underNormalConditions() {

        //given
        userService.createUser(userDto);

        //when
        UserDto newUserDto1 = userService.updateUser(UserDto.builder().id(1).name("User1").email("User1@user.com").build());

        //then
        assertEquals(newUserDto1.getId(), 1);
        assertEquals(newUserDto1.getName(), "User1");
        assertEquals(newUserDto1.getEmail(), "User1@user.com");
    }

    @DirtiesContext
    @Test
    public void update_returnsTheCorrectUserDto_withDuplicateEmail() {

        //given
        userService.createUser(userDto);
        userService.createUser(UserDto.builder().name("UserUserivi4").email("UserUserivi4@user.com").build());

        //then
        assertThrows(DataIntegrityViolationException.class, () -> userService.updateUser(
                UserDto.builder().id(1).email("UserUserivi4@user.com").build()));
    }

    @DirtiesContext
    @Test
    public void delete_returnsNothing_underNormalConditions() {

        //given
        userService.createUser(userDto);
        userService.deleteUser(1);

        //then
        assertEquals(userService.findAllUsers().size(), 0);
    }

    @Test
    public void delete_returnsNothing_inTheAbsenceOfObjects() {

        //then
        assertThrows(EmptyResultDataAccessException.class, () -> userService.deleteUser(1));
        assertEquals(userService.findAllUsers().size(), 0);
    }
}
