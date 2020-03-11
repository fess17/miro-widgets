package ru.kirillspirikhin.mirowidgets.controllers;

import com.marcosbarbero.cloud.autoconfigure.zuul.ratelimit.config.properties.RateLimitProperties;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kirillspirikhin.mirowidgets.config.RateLimitFilter;

/**
 * Администрирование.
 */
@RestController
@RequestMapping("admin")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AdminController {

  /**
   * Фильтр для RateLimit.
   */
  private final RateLimitFilter filter;

  /**
   * Получение настроек RateLimit.
   *
   * @return Настройки RateLimit
   */
  @GetMapping("getPolicy")
  @ApiOperation("Получение настроек RateLimit")
  public RateLimitProperties getPolicy() {
    return filter.getRateLimitProperties();
  }

  /**
   * Установка политики.
   *
   * @param serviceId       ИД сервиса (endpoint)
   * @param limit           лимит запросов
   * @param refreshInterval интервал, на который действует лимит запросов
   * @return измененная политика
   * @apiNote Устанавливает значения лимита запросов и интервала между ними для конкретного сервиса
   *          Если сервис не указан, то будет применено для всех сервисов
   *          Если не указан лимит запросов, то он останется неизменным
   *          Если не указан интервал, на который действует лимит запросов,
   *          то он останется неизменным
   */
  @PostMapping("setPolicy")
  @ApiOperation(value = "Установка политики.",
      notes = "Устанавливает значения лимита запросов"
          + "и интервала между ними для конкретного сервиса\n"
          + "Если сервис не указан, то будет применено для всех сервисов\n"
          + "Если не указан лимит запросов, то он останется неизменным\n"
          + "Если не указан интервал, на который действует лимит запросов,\n"
          + "то он останется неизменным")
  public RateLimitProperties setPolicy(@ApiParam("Идентификатор сервиса")
                                       final String serviceId,
                                       @ApiParam("лимит запросов")
                                       final Long limit,
                                       @ApiParam("интервал, на который действует лимит запросов")
                                       final Long refreshInterval) {
    if (serviceId == null) {
      filter.getRateLimitProperties().getPolicyList()
          .forEach((c, v) -> setPolicy(limit, refreshInterval, v));
    } else {
      List<RateLimitProperties.Policy> policies = Optional.ofNullable(
          filter.getRateLimitProperties().getPolicies(serviceId))
          .orElse(null);
      setPolicy(limit, refreshInterval, policies);
    }
    return filter.getRateLimitProperties();
  }

  /**
   * Установка значений политики.
   *
   * @param limit           лимит запросов
   * @param refreshInterval интервал между запросами
   * @param policies        политики
   */
  private void setPolicy(final Long limit,
                         final Long refreshInterval,
                         final List<RateLimitProperties.Policy> policies) {
    if (policies != null && !policies.isEmpty()) {
      RateLimitProperties.Policy policy = policies.get(0);
      if (limit != null) {
        policy.setLimit(limit);
      }
      if (refreshInterval != null) {
        policy.setRefreshInterval(refreshInterval);
      }
    }
  }
}
