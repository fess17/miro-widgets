package ru.kirillspirikhin.mirowidgets.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

/**
 * Виджет не найден
 */
@RequiredArgsConstructor
@Getter
public class WidgetNotFoundException extends Exception {

    /**
     * ИД виджета
     */
    private final UUID id;

    /**
     * сообщение
     */
    @Override
    public String getMessage() {
        return String.format("Виджет с id = %s не найден", id);
    }
}
