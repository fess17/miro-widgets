package ru.kirillspirikhin.mirowidgets.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Список виджетов для пагинации.
 */
@Data
@AllArgsConstructor
public class PagedWidgets {
  /**
   * Список виджетов.
   */
  private final Widget[] widgets;
  /**
   * Общее количество виджетов.
   */
  private final int totalLength;
  /**
   * Общее количество страниц.
   */
  private final int totalPages;
  /**
   * Текущая страница.
   */
  private final int currentPage;
  /**
   * Размер страницы.
   */
  private final int pageSize;
}
