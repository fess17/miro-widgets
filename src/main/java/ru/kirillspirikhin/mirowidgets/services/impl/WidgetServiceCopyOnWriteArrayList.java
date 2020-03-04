package ru.kirillspirikhin.mirowidgets.services.impl;

import org.springframework.stereotype.Service;
import ru.kirillspirikhin.mirowidgets.exceptions.BadWidgetDescriptionException;
import ru.kirillspirikhin.mirowidgets.exceptions.WidgetNotFoundException;
import ru.kirillspirikhin.mirowidgets.model.Widget;
import ru.kirillspirikhin.mirowidgets.model.WidgetDescription;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Сервис для работы с виджетами
 */
//@Service()
public class WidgetServiceCopyOnWriteArrayList implements ru.kirillspirikhin.mirowidgets.services.WidgetService {

    /**
     * Хранилище виджетов
     */
    private final CopyOnWriteArrayList<Widget> widgets = new CopyOnWriteArrayList<>();

    /**
     * Проверка описания виджета на корректность для добавления
     * @param widgetDescription описание виджета
     * @throws BadWidgetDescriptionException при неполном или неверном описании виджета
     */
    private void checkWidgetDescriptionToAdd(WidgetDescription widgetDescription)
            throws BadWidgetDescriptionException {
        boolean isOk = widgetDescription.getX() != null
                && widgetDescription.getY() != null
                && widgetDescription.getWidth() != null
                && widgetDescription.getHeight() != null;
        if (!isOk) {
            throw new BadWidgetDescriptionException("Неполное описание виджета");
        }
        checkWidgetDescriptionToEdit(widgetDescription);
    }

    /**
     * Проверка описания виджета на корректность для изменения
     * @param widgetDescription описание виджета
     * @throws BadWidgetDescriptionException при неверном описании виджета
     */
    private void checkWidgetDescriptionToEdit(WidgetDescription widgetDescription)
            throws BadWidgetDescriptionException {
        if (widgetDescription.getHeight() != null && widgetDescription.getHeight() < 0) {
            throw new BadWidgetDescriptionException("Высота виджета не может быть меньше 0");
        }
        if (widgetDescription.getWidth() != null && widgetDescription.getWidth() < 0) {
            throw new BadWidgetDescriptionException("Ширина виджета не может быть меньше 0");
        }
    }

    /**
     * Добавление виджета
     * @param widgetDescription описание виджета
     * @throws BadWidgetDescriptionException при неполном или неверном описании виджета
     */
    @Override
    public Widget addWidget(WidgetDescription widgetDescription) {
        int z = getRealZ(widgetDescription.getZ());
        widgetDescription.setZ(z);
        Widget widget = Widget.fromDescription(widgetDescription);
        widget.setModifiedDate(LocalDateTime.now());
        widgets.add(z, widget);
        return widget;
    }

    /**
     * Получение реального Z-order
     * @param neededZ желаемый Z-order
     * @return ресчитанный z-order
     */
    private int getRealZ(final Integer neededZ) {
        int z = 0;
        if (neededZ != null && neededZ > 0) {
            z = Math.min(widgets.size(), neededZ);
        }
        return z;
    }

    /**
     * Получение виджета по его ИД
     * @param id ИД виджета
     * @return Виджет
     * @throws WidgetNotFoundException если виджет не найден
     */
    @Override
    public Widget getById(UUID id) throws WidgetNotFoundException {
        for (Widget widget : widgets) {
            if (widget.getId().equals(id)) {
                return widget;
            }
        }
        throw new WidgetNotFoundException(id);
    }

    /**
     * Изменение виджета по его ИД
     * @param id ИД виджета, коорый необходимо поменять
     * @param description описание изменений
     * @return измененный виджет
     * @throws WidgetNotFoundException если виджет не найден
     * @throws BadWidgetDescriptionException если некорректно описаны новые параметры
     */
    @Override
    public Widget editWidget(UUID id, WidgetDescription description)
            throws WidgetNotFoundException {
        Widget widget = getById(id);
        boolean wasChanges = false;
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
            if (widget.getZ() != description.getZ()) {
                {
                    widget.setZ(getRealZ(description.getZ()));
                    wasChanges = true;
                }
                deleteWidget(id);
                widgets.add(widget.getZ(), widget);
            }
        }
        if (wasChanges) {
            widget.setModifiedDate(LocalDateTime.now());
        }
        return widget;
    }

    /**
     * Удаление виджета
     * @param id ИД виджета
     */
    @Override
    public boolean deleteWidget(UUID id) {
        return widgets.removeIf(wd -> wd.getId().equals(id));
    }

    /**
     * Возвращает список виджетов
     * @return Список виджетов
     */
    @Override
    public Widget[] getAllWidgets() {
        Iterator<Widget> iterator = widgets.iterator();
        Widget[] result = new Widget[widgets.size()];
        int i = 0;
        while (iterator.hasNext()) {
            result[i++] = iterator.next();
        }
        return result;
    }

    @Override
    public void deleteAllWidgets() {
        widgets.clear();
    }
}
