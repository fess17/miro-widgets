package ru.kirillspirikhin.mirowidgets.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Виджет
 */
@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Widget {

    /**
     * ИД
     */
    private final UUID id;

    /**
     * Координата X
     */
    private int x;

    /**
     * Координата Y
     */
    private int y;

    /**
     * Координата Z
     */
    private int z;

    /**
     * Ширина
     */
    private int width;

    /**
     * Высота
     */
    private int height;

    /**
     * Дата последнего изменения
     */
    private LocalDateTime modifiedDate;

    /**
     * Создание виджета из описания
     * @param desc описание виджета {@link WidgetDescription}
     * @return виджет
     */
    public static Widget fromDescription(WidgetDescription desc) {
        Widget widget = Widget.builder()
                .id(UUID.randomUUID())
                .x(desc.getX())
                .y(desc.getY())
                .height(desc.getHeight())
                .width(desc.getWidth())
                .modifiedDate(LocalDateTime.now()).build();
        if (desc.getZ() != null) {
            widget.setZ(desc.getZ());
        }
        return widget;
    }

}
