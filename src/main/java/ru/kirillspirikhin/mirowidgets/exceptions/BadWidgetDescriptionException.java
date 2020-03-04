package ru.kirillspirikhin.mirowidgets.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Неверное описание виджета
 */
@RequiredArgsConstructor
@Getter
public class BadWidgetDescriptionException extends Exception {
    /**
     * сообщение
     */
    private final String message;
}
