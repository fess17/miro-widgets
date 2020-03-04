package ru.kirillspirikhin.mirowidgets;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.kirillspirikhin.mirowidgets.exceptions.WidgetNotFoundException;
import ru.kirillspirikhin.mirowidgets.model.Widget;
import ru.kirillspirikhin.mirowidgets.model.WidgetDescription;
import ru.kirillspirikhin.mirowidgets.services.WidgetService;
import org.junit.jupiter.api.Assertions;
import ru.kirillspirikhin.mirowidgets.services.impl.WidgetServiceLinkedList;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Slf4j
public class WidgetServiceTests {

    /**
     * Сервис для тестирования
     */
    final WidgetService widgetService = new WidgetServiceLinkedList();

    /**
     * Очистим коллекцию для чистоты тестов
     */
    @BeforeEach
    void initList() {
        log.info("initList start");
        widgetService.deleteAllWidgets();
        log.info("initList finish");
    }

    @Test
    @DisplayName("Добавление виджетов")
    void addWidgetsTest() {
        log.info("addWidgetTest start");
        Random r = new Random();
        Widget widget = widgetService.addWidget(WidgetDescription.builder()
                .x(r.nextInt())
                .y(r.nextInt())
                .height(Math.abs(r.nextInt()))
                .width(Math.abs(r.nextInt())).build());
        for (int i = 0; i < 10; i++) {
            widgetService.addWidget(WidgetDescription.builder()
                    .x(r.nextInt())
                    .y(r.nextInt())
                    .z(0)
                    .height(Math.abs(r.nextInt()))
                    .width(Math.abs(r.nextInt())).build());
        }
        Assertions.assertEquals(10, widget.getZ());
        log.info("addWidgetTest finish");
    }

    @Test
    @DisplayName("Удаление виджетов")
    void deleteWidgetTest() {
        log.info("deleteWidgetTest start");
        Random r = new Random();
        Widget widget = widgetService.addWidget(WidgetDescription.builder()
                .x(r.nextInt())
                .y(r.nextInt())
                .height(Math.abs(r.nextInt()))
                .width(Math.abs(r.nextInt())).build());
        boolean deleteWidget = widgetService.deleteWidget(widget.getId());
        Assertions.assertTrue(deleteWidget);
        log.info("deleteWidgetTest finish");
    }

    @Test
    @DisplayName("Получение виджета по ИД")
    void getWidgetByIdTest() {
        log.info("getWidgetByIdTest start");
        Random r = new Random();
        try {
            Widget widget = widgetService.addWidget(WidgetDescription.builder()
                    .x(r.nextInt())
                    .y(r.nextInt())
                    .height(Math.abs(r.nextInt()))
                    .width(Math.abs(r.nextInt())).build());
            Widget widget1 = widgetService.getById(widget.getId());
            Assertions.assertEquals(widget.getId(), widget1.getId());
        } catch (WidgetNotFoundException e) {
            Assertions.fail(e.getMessage());
        }
        log.info("getWidgetByIdTest finish");
    }

    @Test
    @DisplayName("Получение всех виджетов")
    void getAllWidgetsTest() {
        log.info("getAllWidgetsTest start");
        Random r = new Random();
        widgetService.addWidget(WidgetDescription.builder()
                .x(r.nextInt())
                .y(r.nextInt())
                .height(Math.abs(r.nextInt()))
                .width(Math.abs(r.nextInt())).build());
        widgetService.addWidget(WidgetDescription.builder()
                .x(r.nextInt())
                .y(r.nextInt())
                .height(Math.abs(r.nextInt()))
                .width(Math.abs(r.nextInt())).build());
        Assertions.assertEquals(2, widgetService.getAllWidgets().length);
        log.info("getAllWidgetsTest finish");
    }

    @Test
    @DisplayName("Редактирование виджета")
    void editWidgetTest() {
        log.info("editWidgetTest start");
        Random r = new Random();
        try {
            Widget w = widgetService.addWidget(WidgetDescription.builder()
                    .x(r.nextInt())
                    .y(r.nextInt())
                    .height(Math.abs(r.nextInt()))
                    .width(Math.abs(r.nextInt())).build());
            UUID widgetId = w.getId();
            LocalDateTime modifiedDate = w.getModifiedDate();
            WidgetDescription widgetDescription = WidgetDescription.builder()
                    .x(r.nextInt())
                    .y(r.nextInt())
                    .z(w.getZ())
                    .height(Math.abs(r.nextInt()))
                    .width(Math.abs(r.nextInt())).build();
            Thread.sleep(1); //-- если этого нет, время не меняется
            Widget w1 = widgetService.editWidget(w.getId(), widgetDescription);
            Assertions.assertAll("check edit widget",
                    () -> Assertions.assertEquals(w, w1),
                    () -> Assertions.assertNotEquals(w.getModifiedDate(), modifiedDate),
                    () -> Assertions.assertEquals(w.getId(), widgetId),
                    () -> Assertions.assertEquals(w.getX(), widgetDescription.getX()),
                    () -> Assertions.assertEquals(w.getY(), widgetDescription.getY()),
                    () -> Assertions.assertEquals(w.getZ(), widgetDescription.getZ()),
                    () -> Assertions.assertEquals(w.getHeight(), widgetDescription.getHeight()),
                    () -> Assertions.assertEquals(w.getWidth(), widgetDescription.getWidth()),
                    () -> Assertions.assertNotEquals(w1.getModifiedDate(), modifiedDate));
        } catch (WidgetNotFoundException | InterruptedException e) {
            Assertions.fail(e.getMessage());
        }
        log.info("editWidgetTest finish");
    }
}
