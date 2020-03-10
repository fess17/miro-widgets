package ru.kirillspirikhin.mirowidgets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.kirillspirikhin.mirowidgets.exceptions.WidgetNotFoundException;
import ru.kirillspirikhin.mirowidgets.model.PagedWidgets;
import ru.kirillspirikhin.mirowidgets.model.Widget;
import ru.kirillspirikhin.mirowidgets.model.WidgetDescription;
import ru.kirillspirikhin.mirowidgets.services.WidgetService;
import ru.kirillspirikhin.mirowidgets.services.impl.WidgetServiceLinkedList;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@DisplayName("Тесты конкурентности")
public class WidgetServiceConcurrentTests {

  /**
   * Сервис для тестирования
   */
  final WidgetService widgetService = new WidgetServiceLinkedList();

  /**
   * Количество операций
   */
  final int OPERATIONS_COUNT = 100_000;

  /**
   * Тест конкурентности
   */
  @Test
  @DisplayName("Тест конкурентности")
  public void concurrentTest() {
    log.info("concurrentTest start");
    final CountDownLatch doneLatch = new CountDownLatch(OPERATIONS_COUNT);
    Map<String, AtomicInteger> operations = new HashMap<>(5);
    operations.put("addWidget", new AtomicInteger());
    operations.put("getById", new AtomicInteger());
    operations.put("editWidget", new AtomicInteger());
    operations.put("deleteWidget", new AtomicInteger());
    operations.put("getAllWidgets", new AtomicInteger());
    AtomicInteger exceptionsCount = new AtomicInteger();
    Random r = new Random();
    try {
      for (int operation = 0; operation < OPERATIONS_COUNT; operation++) {
        int q = r.nextInt(10);
        switch (q) {
          case 0:
            new Thread(() -> {
              Random r1 = new Random();
              widgetService.addWidget(WidgetDescription.builder()
                  .x(r1.nextInt())
                  .y(r1.nextInt())
                  .z(r1.nextInt(OPERATIONS_COUNT))
                  .height(Math.abs(r1.nextInt()))
                  .width(Math.abs(r1.nextInt())).build());
              operations.get("addWidget").incrementAndGet();
              doneLatch.countDown();
            }, "addWidgetTest").start();
            break;
          case 1:
            new Thread(() -> {
              Random r1 = new Random();
              try {
                WidgetDescription widgetDescription = WidgetDescription.builder()
                    .x(r1.nextInt())
                    .y(r1.nextInt())
                    .z(r1.nextInt(OPERATIONS_COUNT))
                    .height(Math.abs(r1.nextInt()))
                    .width(Math.abs(r1.nextInt())).build();
                PagedWidgets widgetsPage = widgetService.getAllWidgets();
                Widget[] widgets = widgetsPage.getWidgets();
                operations.get("getAllWidgets").incrementAndGet();
                if (widgets.length > 0) {
                  Widget widget = widgetService.getById(widgets[r1.nextInt(widgets.length)].getId());
                  operations.get("getById").incrementAndGet();
                  widgetService.editWidget(widget.getId(), widgetDescription);
                  operations.get("editWidget").incrementAndGet();
                }
              } catch (WidgetNotFoundException e) {
                log.info(e.toString());
                exceptionsCount.incrementAndGet();
              }
              doneLatch.countDown();
            }, "editWidgetTest").start();
            break;
          case 2:
            new Thread(() -> {
              Random r1 = new Random();
              try {
                PagedWidgets widgetsPage = widgetService.getAllWidgets();
                Widget[] widgets = widgetsPage.getWidgets();
                operations.get("getAllWidgets").incrementAndGet();
                if (widgets.length > 0) {
                  Widget widget = widgetService.getById(widgets[r1.nextInt(widgets.length)].getId());
                  operations.get("getById").incrementAndGet();
                  widgetService.deleteWidget(widget.getId());
                  operations.get("deleteWidget").incrementAndGet();
                }
              } catch (WidgetNotFoundException e) {
                log.info(e.toString());
                exceptionsCount.incrementAndGet();
              }
              doneLatch.countDown();
            }, "deleteWidgetTest").start();
            break;
          case 3:
            new Thread(() -> {
              Random r1 = new Random();
              try {
                PagedWidgets widgetsPage = widgetService.getAllWidgets();
                Widget[] widgets = widgetsPage.getWidgets();
                operations.get("getAllWidgets").incrementAndGet();
                if (widgets.length > 0) {
                  widgetService.getById(widgets[r1.nextInt(widgets.length)].getId());
                  operations.get("getById").incrementAndGet();
                }
              } catch (WidgetNotFoundException e) {
                log.info(e.toString());
                exceptionsCount.incrementAndGet();
              }
              doneLatch.countDown();
            }, "getWidgetByIdTest").start();
            break;
          default:
            new Thread(() -> {
              widgetService.getAllWidgets();
              operations.get("getAllWidgets").incrementAndGet();
              doneLatch.countDown();
            }, "getAllWidgetsTest").start();
            break;
        }
      }
      doneLatch.await();
      ObjectMapper om = new ObjectMapper();
      om.registerModule(new JavaTimeModule());
      om.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
      PagedWidgets widgetsPage = widgetService.getAllWidgets();
      Widget[] allWidgets = widgetsPage.getWidgets();
      log.info("allWidgets.length = " + allWidgets.length);
      log.info("operations = " + om.writeValueAsString(operations));
      String zs = om.writeValueAsString(Arrays.stream(allWidgets).map(Widget::getZ).collect(Collectors.toList()));
      String orderedZs = om.writeValueAsString(Arrays.stream(allWidgets).map(Widget::getZ)
          .sorted(Integer::compareTo).collect(Collectors.toList()));
      log.info("exceptionsCount = " + exceptionsCount);
      Assertions.assertEquals(zs, orderedZs);
    } catch (Exception e) {
      Assertions.fail(e.toString());
    }
    log.info("concurrentTest finish");
  }
}
