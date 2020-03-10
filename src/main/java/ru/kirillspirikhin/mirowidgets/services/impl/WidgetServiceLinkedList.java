package ru.kirillspirikhin.mirowidgets.services.impl;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;
import javax.naming.OperationNotSupportedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kirillspirikhin.mirowidgets.exceptions.WidgetNotFoundException;
import ru.kirillspirikhin.mirowidgets.model.Widget;
import ru.kirillspirikhin.mirowidgets.model.WidgetDescription;
import ru.kirillspirikhin.mirowidgets.services.WidgetService;

/**
 * Реализация сервиса виджетов на {@link java.util.LinkedList}.
 */
@Slf4j
@Service
public class WidgetServiceLinkedList implements WidgetService {

  /**
   * The lock protecting all mutators.
   */
  final ReentrantLock reentrantLock = new ReentrantLock();

  /**
   * Хранилище виджетов.
   */
  private final LinkedList<Widget> widgets = new LinkedList<>();

  @Override
  public Widget addWidget(WidgetDescription widgetDescription) {
    boolean insetAsLast = widgetDescription.getZ() == null;
    Widget widget = Widget.fromDescription(widgetDescription);
    widget.setModifiedDate(LocalDateTime.now());
    final ReentrantLock lock = this.reentrantLock;
    lock.lock();
    try {
      addWidgetInternal(widget, insetAsLast);
    } catch (OperationNotSupportedException e) {
      log.error(e.toString());
    } finally {
      lock.unlock();
    }
    return widget;
  }

  /**
   * Добавление виджета в коллекцию.
   *
   * @param widget       виджет
   * @param insertAsLast вставка в конец (не указан Z-order)
   * @throws OperationNotSupportedException исключение
   */
  private void addWidgetInternal(final Widget widget, final boolean insertAsLast)
      throws OperationNotSupportedException {
    if (insertAsLast) {
      /* если у добавляемого виджета не указан Z-order,
      то присвоим Z-order на 1 больше, чем у последнего */
      widget.setZ(!widgets.isEmpty()
          ? widgets.getLast().getZ() + 1
          : 0);
    }
    if (widgets.isEmpty() || widget.getZ() > widgets.getLast().getZ()) {
      /* если список пуст или если у добавляемого виджета заведомо больший Z-order,
      то просто вставляем */
      widgets.add(widget);
    } else if (widget.getZ() <= widgets.getFirst().getZ()) {
      /* если все элементы имеют Z-order больше, чем у вставляемого */
      widgets.forEach(w -> w.setZ(w.getZ() + 1));
      widgets.add(0, widget);
    } else {
      /* ищем, куда вставить добавляемый виджет и сдвигаем Z-order у тех виджетов,
      у которых он больше либо равен вставляемому */
      boolean positionFound = false;
      ListIterator<Widget> iterator = widgets.listIterator();
      while (iterator.hasNext()) {
        Widget currentWidget = iterator.next();
        if (currentWidget.getZ() >= widget.getZ()) {
          if (!positionFound) {
            iterator.previous();
            iterator.add(widget);
            positionFound = true;
            iterator.next();
          }
          currentWidget.setZ(currentWidget.getZ() + 1);
        }
      }
      if (!positionFound) {
        /* если так и не вставили, значит бага в алгоритме */
        throw new OperationNotSupportedException("Ошибка в алгоритме");
      }
    }
  }

  @Override
  public Widget getById(UUID id) throws WidgetNotFoundException {
    Widget[] local = getAllWidgets();
    for (Widget w : local) {
      if (w.getId().equals(id)) {
        return w;
      }
    }
    throw new WidgetNotFoundException(id);
  }

  @Override
  public Widget editWidget(UUID id, WidgetDescription description)
      throws WidgetNotFoundException {
    Widget widget = null;
    final ReentrantLock lock = this.reentrantLock;
    lock.lock();
    try {
      widget = widgets.stream().filter(w -> w.getId().equals(id)).findFirst()
          .orElseThrow(() -> new WidgetNotFoundException(id));
      boolean wasChanges = false;
      boolean needMove = false;
      if (description.getX() != null) {
        widget.setX(description.getX());
        wasChanges = true;
      }
      if (description.getY() != null) {
        widget.setY(description.getY());
        wasChanges = true;
      }
      if (description.getHeight() != null) {
        widget.setHeight(description.getHeight());
        wasChanges = true;
      }
      if (description.getWidth() != null) {
        widget.setWidth(description.getWidth());
        wasChanges = true;
      }
      if (description.getZ() != null) {
        needMove = !description.getZ().equals(widget.getZ());
        widget.setZ(description.getZ());
        wasChanges = true;
      }
      if (wasChanges) {
        widget.setModifiedDate(LocalDateTime.now());
        if (needMove) {
          if (widgets.removeIf(w -> w.getId().equals(id))) {
            addWidgetInternal(widget, false);
          } else {
            return null;
          }
        }
      }
    } catch (OperationNotSupportedException e) {
      log.error(e.toString());
    } finally {
      lock.unlock();
    }
    return widget;
  }

  @Override
  public boolean deleteWidget(UUID id) {
    final ReentrantLock lock = this.reentrantLock;
    lock.lock();
    try {
      return widgets.removeIf(w -> w.getId().equals(id));
    } finally {
      lock.unlock();
    }
  }

  @Override
  public Widget[] getAllWidgets() {
    Widget[] localWidgets;
    final ReentrantLock lock = this.reentrantLock;
    lock.lock();
    try {
      localWidgets = new Widget[widgets.size()];
      localWidgets = widgets.toArray(localWidgets);
    } finally {
      lock.unlock();
    }
    return localWidgets;
  }

  @Override
  public void deleteAllWidgets() {
    final ReentrantLock lock = this.reentrantLock;
    lock.lock();
    try {
      widgets.clear();
    } finally {
      lock.unlock();
    }
  }
}
