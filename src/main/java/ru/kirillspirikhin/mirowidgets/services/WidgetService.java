package ru.kirillspirikhin.mirowidgets.services;

import ru.kirillspirikhin.mirowidgets.exceptions.WidgetNotFoundException;
import ru.kirillspirikhin.mirowidgets.model.Widget;
import ru.kirillspirikhin.mirowidgets.model.WidgetDescription;

import java.util.UUID;

/**
 * Сервис для работы с виджетами
 */
public interface WidgetService {
    /**
     * Доабвить виджет
     * @param widgetDescription описание виджета
     * @return добавленный виджет
     */
    Widget addWidget(WidgetDescription widgetDescription);

    /**
     * Получить виджет по его ИД
     * @param id ИД виджета
     * @return виджет
     * @throws WidgetNotFoundException если виджет не найден
     */
    Widget getById(UUID id) throws WidgetNotFoundException;

    /**
     * Редактировать виджета
     * @param id ИД виджета
     * @param description описание виджета
     * @return отредактированный видджет
     * @throws WidgetNotFoundException если виджет не найден
     */
    Widget editWidget(UUID id, WidgetDescription description)
            throws WidgetNotFoundException;

    /**
     * Удалить виджет
     * @param id ИД виджета
     * @return признак того, что виджет с аким ИД был удален
     */
    boolean deleteWidget(UUID id);

    /**
     * Получить все виджеты
     * @return список виджетов
     */
    Widget[] getAllWidgets();

    /**
     * Удалить все виджеты
     */
    void deleteAllWidgets();
}
