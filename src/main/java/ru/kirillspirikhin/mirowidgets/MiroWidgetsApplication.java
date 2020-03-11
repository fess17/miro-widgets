package ru.kirillspirikhin.mirowidgets;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@EnableZuulProxy
public class MiroWidgetsApplication {

  public static void main(String[] args) {
    SpringApplication.run(MiroWidgetsApplication.class, args);
  }

}
