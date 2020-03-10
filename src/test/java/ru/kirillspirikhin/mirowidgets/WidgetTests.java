package ru.kirillspirikhin.mirowidgets;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.kirillspirikhin.mirowidgets.model.Widget;
import ru.kirillspirikhin.mirowidgets.model.WidgetDescription;

import java.util.Random;

/**
 * Тестирование класса {@link Widget}.
 */
@Slf4j
public class WidgetTests {

  @Test
  void fromDescriptionTest() {
    log.info("fromDescriptionTest start");
    Random r = new Random();
    WidgetDescription widgetDescription = WidgetDescription.builder()
        .x(r.nextInt())
        .y(r.nextInt())
        .z(r.nextInt())
        .height(Math.abs(r.nextInt()))
        .width(Math.abs(r.nextInt())).build();
    final Widget w = Widget.fromDescription(widgetDescription);
    Assertions.assertAll("Widget equals widgetDescription",
        () -> Assertions.assertEquals(w.getX(), widgetDescription.getX()),
        () -> Assertions.assertEquals(w.getY(), widgetDescription.getY()),
        () -> Assertions.assertEquals(w.getZ(), widgetDescription.getZ()),
        () -> Assertions.assertEquals(w.getWidth(), widgetDescription.getWidth()),
        () -> Assertions.assertEquals(w.getHeight(), widgetDescription.getHeight()));
    log.info("fromDescriptionTest finish");
  }
}
