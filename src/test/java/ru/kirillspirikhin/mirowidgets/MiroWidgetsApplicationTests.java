package ru.kirillspirikhin.mirowidgets;

import com.jayway.jsonpath.JsonPath;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.kirillspirikhin.mirowidgets.model.WidgetDescription;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
@DisplayName("Тестирование контроллера")
class MiroWidgetsApplicationTests {

    private final MockMvc mockMvc;

    @Test
    @DisplayName("Поднятие контекста")
    void contextLoads() {
        Assertions.assertTrue(true);
    }

    @Test
    @DisplayName("Успешное создание виджета")
    void createWidgetOk() throws Exception {
        WidgetDescription wd = WidgetDescription.builder()
                .x(0)
                .y(1)
                .z(2)
                .height(3)
                .width(4).build();
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/create")
                .param("x", String.valueOf(wd.getX()))
                .param("y", String.valueOf(wd.getY()))
                .param("z", String.valueOf(wd.getZ()))
                .param("height", String.valueOf(wd.getHeight()))
                .param("width", String.valueOf(wd.getWidth()))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String res = result.getResponse().getContentAsString();
        log.info(res);
        assertNotEquals("", res);
    }

    @Test
    @DisplayName("Неверное описание виджета при создании")
    void createWidgetPreconditionFailed() throws Exception {
        WidgetDescription wd = WidgetDescription.builder()
                .x(0)
                .y(1)
                .z(2)
                .height(-3)
                .width(4).build();
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/create")
                .characterEncoding("UTF-8")
                .param("x", String.valueOf(wd.getX()))
                .param("y", String.valueOf(wd.getY()))
                .param("z", String.valueOf(wd.getZ()))
                .param("height", String.valueOf(wd.getHeight()))
                .param("width", String.valueOf(wd.getWidth()))
                .accept(MediaType.ALL))
                .andExpect(status().isPreconditionFailed())
                .andReturn();
        String res = result.getResponse().getContentAsString();
        log.info(res);
        assertNotEquals("", res);
    }

    @Test
    @DisplayName("Успешное получение виджета по его id")
    void getWidgetByIdOk() throws Exception {
        WidgetDescription wd = WidgetDescription.builder()
                .x(0)
                .y(1)
                .z(2)
                .height(3)
                .width(4).build();
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/create")
                .param("x", String.valueOf(wd.getX()))
                .param("y", String.valueOf(wd.getY()))
                .param("z", String.valueOf(wd.getZ()))
                .param("height", String.valueOf(wd.getHeight()))
                .param("width", String.valueOf(wd.getWidth()))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String res = result.getResponse().getContentAsString();
        log.info(res);
        UUID id = UUID.fromString(JsonPath.parse(res).read("$.id"));
        result = mockMvc.perform(
            MockMvcRequestBuilders.get("/get/{id}", id)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id.toString()))
            .andReturn();
        String res1 = result.getResponse().getContentAsString();
        log.info(res1);
        assertEquals(res1, res);
    }

    @Test
    @DisplayName("Виджет с таким id не найден")
    void getWidgetByIdNotFound() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
            MockMvcRequestBuilders.get("/get/{id}", UUID.randomUUID())
                .characterEncoding("UTF-8")
                .accept(MediaType.ALL))
                .andExpect(status().isNotFound())
                .andReturn();
        String res = mvcResult.getResponse().getContentAsString();
        log.info(res);
        assertNotEquals("", res);
    }

    @Test
    @DisplayName("Успешное редактирование виджета")
    void editWidgetOk() throws Exception {
        WidgetDescription wd = WidgetDescription.builder()
                .x(0)
                .y(1)
                .z(2)
                .height(3)
                .width(4).build();
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/create")
                .param("x", String.valueOf(wd.getX()))
                .param("y", String.valueOf(wd.getY()))
                .param("z", String.valueOf(wd.getZ()))
                .param("height", String.valueOf(wd.getHeight()))
                .param("width", String.valueOf(wd.getWidth()))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String res = result.getResponse().getContentAsString();
        log.info(res);
        UUID id1 = UUID.fromString(JsonPath.parse(res).read("$.id"));
        LocalDateTime ldt1 = LocalDateTime.parse(JsonPath.parse(res).read("$.modifiedDate"));
        wd = WidgetDescription.builder()
                .x(1)
                .y(2)
                .z(3)
                .height(4)
                .width(5).build();
        result = mockMvc.perform(MockMvcRequestBuilders.patch("/edit/{id}", id1)
                .param("x", String.valueOf(wd.getX()))
                .param("y", String.valueOf(wd.getY()))
                .param("z", String.valueOf(wd.getZ()))
                .param("height", String.valueOf(wd.getHeight()))
                .param("width", String.valueOf(wd.getWidth()))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        res = result.getResponse().getContentAsString();
        log.info(res);
        UUID id2 = UUID.fromString(JsonPath.parse(res).read("$.id"));
        LocalDateTime ldt2 = LocalDateTime.parse(JsonPath.parse(res).read("$.modifiedDate"));
        int x = JsonPath.parse(res).read("$.x");
        int y = JsonPath.parse(res).read("$.y");
        int z = JsonPath.parse(res).read("$.z");
        int height = JsonPath.parse(res).read("$.height");
        int width = JsonPath.parse(res).read("$.width");
        assertEquals(id1, id2);
        assertNotEquals(ldt1, ldt2);
        assertEquals(wd.getX(), x);
        assertEquals(wd.getY(), y);
        assertEquals(wd.getZ(), z);
        assertEquals(wd.getHeight(), height);
        assertEquals(wd.getWidth(), width);
    }

    @Test
    @DisplayName("Попытка редактирования несуществующего виджета")
    void editWidgetNotFound() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch("/edit/{id}", UUID.randomUUID())
                .characterEncoding("UTF-8")
                .accept(MediaType.ALL))
                .andExpect(status().isNotFound())
                .andReturn();
        String res = mvcResult.getResponse().getContentAsString();
        log.info(res);
        assertNotEquals("", res);
    }

    @Test
    @DisplayName("Редактирование виджета с некорректными параметрами")
    void editWidgetPreconditionFailed() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch("/edit/{id}", UUID.randomUUID())
                .characterEncoding("UTF-8")
                .param("height", String.valueOf(-1))
                .accept(MediaType.ALL))
                .andExpect(status().isPreconditionFailed())
                .andReturn();
        String res = mvcResult.getResponse().getContentAsString();
        log.info(res);
        assertNotEquals("", res);
    }

    @Test
    @DisplayName("Успешное удаление виджета")
    void deleteWidgetOk() throws Exception {
        WidgetDescription wd = WidgetDescription.builder()
                .x(0)
                .y(1)
                .z(2)
                .height(3)
                .width(4).build();
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/create")
                .param("x", String.valueOf(wd.getX()))
                .param("y", String.valueOf(wd.getY()))
                .param("z", String.valueOf(wd.getZ()))
                .param("height", String.valueOf(wd.getHeight()))
                .param("width", String.valueOf(wd.getWidth()))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String res = result.getResponse().getContentAsString();
        log.info(res);
        UUID id = UUID.fromString(JsonPath.parse(res).read("$.id"));
        result = mockMvc.perform(
                MockMvcRequestBuilders.delete("/delete/{id}", id)
                        .characterEncoding("UTF-8")
                        .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andReturn();
        res = result.getResponse().getContentAsString();
        log.info(res);
        assertEquals("true", res);
    }

    @Test
    @DisplayName("Попытка удаления несуществующего виджета")
    void deleteWidgetNotFound() throws Exception {
        MvcResult result = mockMvc.perform(
            MockMvcRequestBuilders.delete("/delete/{id}", UUID.randomUUID())
                .characterEncoding("UTF-8")
                .accept(MediaType.ALL))
                .andExpect(status().isNotFound())
                .andReturn();
        String res = result.getResponse().getContentAsString();
        log.info(res);
        assertNotEquals("", res);
    }

    @Test
    @DisplayName("Получение всех виджетов")
    void getAllWidgets() throws Exception {
        WidgetDescription wd = WidgetDescription.builder()
                .x(0)
                .y(1)
                .z(2)
                .height(3)
                .width(4).build();
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/create")
                .param("x", String.valueOf(wd.getX()))
                .param("y", String.valueOf(wd.getY()))
                .param("z", String.valueOf(wd.getZ()))
                .param("height", String.valueOf(wd.getHeight()))
                .param("width", String.valueOf(wd.getWidth()))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String res = result.getResponse().getContentAsString();
        UUID id1 = UUID.fromString(JsonPath.parse(res).read("$.id"));
        result = mockMvc.perform(MockMvcRequestBuilders.put("/create")
                .param("x", String.valueOf(wd.getX()))
                .param("y", String.valueOf(wd.getY()))
                .param("z", String.valueOf(wd.getZ()))
                .param("height", String.valueOf(wd.getHeight()))
                .param("width", String.valueOf(wd.getWidth()))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        res = result.getResponse().getContentAsString();
        UUID id2 = UUID.fromString(JsonPath.parse(res).read("$.id"));
        log.info(res);
        result = mockMvc.perform(MockMvcRequestBuilders.get("/getAll")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        res = result.getResponse().getContentAsString();
        UUID id11 = UUID.fromString(JsonPath.parse(res).read("$[1].id"));
        UUID id21 = UUID.fromString(JsonPath.parse(res).read("$[0].id"));
        assertNotEquals("", res);
        assertEquals(id1.toString(), id11.toString());
        assertEquals(id2.toString(), id21.toString());
    }

}
