package ru.kirillspirikhin.mirowidgets.model;

import lombok.*;

/**
 * Описание виджета
 */
@Data
@Builder
@AllArgsConstructor
public class WidgetDescription {
    /**
     * Координата X
     */
    private Integer x;

    /**
     * Координата Y
     */
    private Integer y;

    /**
     * Координата Z
     */
    private Integer z;

    /**
     * Ширина
     */
    private Integer width;

    /**
     * Высота
     */
    private Integer height;
}
