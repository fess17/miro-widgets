package ru.kirillspirikhin.mirowidgets.controllers;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kirillspirikhin.mirowidgets.exceptions.BadWidgetDescriptionException;
import ru.kirillspirikhin.mirowidgets.exceptions.WidgetNotFoundException;
import ru.kirillspirikhin.mirowidgets.model.Widget;
import ru.kirillspirikhin.mirowidgets.model.WidgetDescription;
import ru.kirillspirikhin.mirowidgets.services.WidgetService;

import java.util.UUID;

/**
 * Контроллер для работы с виджетами
 */
@RestController()
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class WidgetController {

    /**
     * Сервис для работы с виджетами
     */
    private final WidgetService widgetService;

    @PutMapping("create")
    @ApiOperation("Добавление виджета")
    public ResponseEntity<Widget> createWidget(WidgetDescription description)
            throws BadWidgetDescriptionException {
        String checkDescription = checkWidgetDescriptionToAdd(description);
        if (!"".equals(checkDescription)) {
            throw new BadWidgetDescriptionException("Некорректное описание виджета:" + checkDescription);
        }
        Widget widget = widgetService.addWidget(description);
        return new ResponseEntity<>(widget, HttpStatus.OK);
    }

    @GetMapping("get/{id}")
    @ApiOperation("Получение виджета по его ИД")
    public ResponseEntity<Widget> getWidgetById(@ApiParam("Идентификатор виджета") @PathVariable UUID id)
            throws WidgetNotFoundException {
        Widget widget = widgetService.getById(id);
        return new ResponseEntity<>(widget, HttpStatus.OK);
    }

    @PatchMapping("edit/{id}")
    @ApiOperation("Редактирование виджета")
    public ResponseEntity<Widget> editWidget(@ApiParam("Идентификатор виджета") @PathVariable UUID id,
                                             WidgetDescription description)
            throws BadWidgetDescriptionException, WidgetNotFoundException {
        String checkDescription = checkWidgetDescriptionToEdit(description);
        if (!"".equals(checkDescription)) {
            throw new BadWidgetDescriptionException("Некорректное описание виджета:" + checkDescription);
        }
        Widget widget = widgetService.editWidget(id, description);
        return new ResponseEntity<>(widget, HttpStatus.OK);
    }

    @DeleteMapping("delete/{id}")
    @ApiOperation("Удаление виджета по его ИД")
    public ResponseEntity<Boolean> deleteWidget(@ApiParam("Идентификатор виджета") @PathVariable UUID id)
            throws WidgetNotFoundException {
        boolean deleted = widgetService.deleteWidget(id);
        if (!deleted) {
            throw new WidgetNotFoundException(id);
        }
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @GetMapping("getAll")
    @ApiOperation("Получение всех виджетов")
    public Widget[] getAllWidgets() {
        return widgetService.getAllWidgets();
    }

    @ExceptionHandler(BadWidgetDescriptionException.class)
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    public String handle(BadWidgetDescriptionException e) {
        return e.getMessage();
    }

    @ExceptionHandler(WidgetNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handle(WidgetNotFoundException e) {
        return e.getMessage();
    }

    /**
     * Проверка описания виджета на корректность для добавления
     * @param widgetDescription описание виджета
     * @return Описание ошибок
     */
    private String checkWidgetDescriptionToAdd(WidgetDescription widgetDescription) {
        StringBuilder sb = new StringBuilder();
        if (widgetDescription.getX() == null) {
            sb.append(System.lineSeparator());
            sb.append("координата X должна быть указана");
        }
        if (widgetDescription.getY() == null) {
            sb.append(System.lineSeparator());
            sb.append("координата Y должна быть указана");
        }
        if (widgetDescription.getWidth() == null) {
            sb.append(System.lineSeparator());
            sb.append("ширина должна быть указана");
        }
        if (widgetDescription.getHeight() == null) {
            sb.append(System.lineSeparator());
            sb.append("высота должна быть указана");
        }
        sb.append(checkWidgetDescriptionToEdit(widgetDescription));
        return sb.toString();
    }

    /**
     * Проверка описания виджета на корректность для изменения
     * @param widgetDescription описание виджета
     * @return Описание ошибок
     */
    private String checkWidgetDescriptionToEdit(WidgetDescription widgetDescription) {
        StringBuilder sb = new StringBuilder();
        if (widgetDescription.getHeight() != null && widgetDescription.getHeight() < 0) {
            sb.append(System.lineSeparator());
            sb.append("высота не может быть меньше 0");
        }
        if (widgetDescription.getWidth() != null && widgetDescription.getWidth() < 0) {
            sb.append(System.lineSeparator());
            sb.append("ширина не может быть меньше 0");
        }
        return sb.toString();
    }
}
