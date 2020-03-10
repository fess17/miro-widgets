package ru.kirillspirikhin.mirowidgets.exceptions;

import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Виджет не найден.
 */
@RequiredArgsConstructor
@Getter
public class WidgetNotFoundException extends Exception {

  /**
   * ИД виджета.
   */
  private final UUID id;

  /**
   * Сообщение.
   */
  @Override
  public String getMessage() {
    return String.format("Виджет с id = %s не найден", id);
  }
}
