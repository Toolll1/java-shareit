package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class UserDtoJsonTest {

    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void testUserDto() throws Exception {

        //given
        UserDto userDto = UserDto.builder().id(1).name("User1").email("User1@user.com").build();

        //when
        JsonContent<UserDto> result = json.write(userDto);

        //then
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("User1");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("User1@user.com");
    }
}
