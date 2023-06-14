package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void testUserDto() throws Exception {

        //given
        ItemDto itemDto = ItemDto.builder().id(1).name("дрель").description("Простая дрель").available(true).build();

        //when
        JsonContent<ItemDto> result = json.write(itemDto);

        //then
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("дрель");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Простая дрель");
    }
}
