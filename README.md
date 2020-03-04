# Miro Widgets

### Предметная область
[Задание](https://docs.google.com/document/d/1-4Gor5-k5ORPhcsPC70W_DjEQcaF2c7KcMUPPe16Eik/edit#heading=h.siniuthmcgrd)

### Описание реализации
Maven-проект состоит из одного модуля.

<b>Для локальной сборки проекта необходимо, чтобы было установленно следующее ПО:</b>
- JDK 1.8
- Maven 3

<b>Cборка Maven-проекта:</b>
- в корневом каталоге проекта выполнить: "mvn clean install"

<b>Результат сборки Maven-проекта:</b>
- JAR-файл для приложения: target/miro-widgets-0.0.1-SNAPSHOT.jar

<b>Порядок локального запуска приложения:</b>
1. В каталоге проекта выполнить: "mvn spring-boot:run";
2. В веб-браузере открыть http://localhost:8079/api/swagger-ui.html - будет отображена страница swagger с описанием API.

<b>Порядок развертывания приложения:</b>
- На узле 1 разворачивается приложение и запускается: "java -jar miro-widgets-0.0.1-SNAPSHOT.jar". <br>
