package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserMapperTests {

    private final UserMapper userMapper;

    @Test
    public void objectToDto() {

        User user = User.builder().name("user").email("user@user.com").build();
        UserDto userDto = UserMapper.objectToDto(user);

        assertEquals(userDto.getName(), user.getName());
        assertEquals(userDto.getEmail(), user.getEmail());
    }

    @Test
    public void dtoToObject() {

        UserDto userDto = UserDto.builder().name("user").email("user@user.com").build();
        User user = userMapper.dtoToObject(userDto);

        assertEquals(userDto.getName(), user.getName());
        assertEquals(userDto.getEmail(), user.getEmail());
    }
}
