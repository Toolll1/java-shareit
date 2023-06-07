package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void testUserDto() throws Exception {

        //given
        ItemRequestDto itemRequestDto = ItemRequestDto.builder().id(1).description("Хотел бы воспользоваться щёткой для обуви").build();

        //when
        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        //then
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Хотел бы воспользоваться щёткой для обуви");
    }
}
