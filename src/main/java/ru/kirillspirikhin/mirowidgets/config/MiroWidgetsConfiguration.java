package ru.kirillspirikhin.mirowidgets.config;

import com.marcosbarbero.cloud.autoconfigure.zuul.ratelimit.config.RateLimitKeyGenerator;
import com.marcosbarbero.cloud.autoconfigure.zuul.ratelimit.config.RateLimitUtils;
import com.marcosbarbero.cloud.autoconfigure.zuul.ratelimit.config.RateLimiter;
import com.marcosbarbero.cloud.autoconfigure.zuul.ratelimit.config.properties.RateLimitProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.UrlPathHelper;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * Конфигурация приложения.
 */
@Configuration
public class MiroWidgetsConfiguration {
  /**
   * Бин для swagger.
   *
   * @return ярлык/этикетка/как там лучше то назвать
   */
  @Bean
  public Docket swagger() {
    return new Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(RequestHandlerSelectors.any())
        .paths(PathSelectors.any())
        .build();
  }

  /**
   * Бин для RateLimit.
   *
   * @param properties настройки
   * @param routeLocator routeLocator
   * @param urlPathHelper urlPathHelper
   * @param rateLimiter rateLimiter
   * @param rateLimitKeyGenerator rateLimitKeyGenerator
   * @param rateLimitUtils rateLimitUtils
   * @return Фильтр для RateLimit
   */
  @Bean
  public RateLimitFilter rateLimitFilter(final RateLimitProperties properties,
                                         final RouteLocator routeLocator,
                                         final UrlPathHelper urlPathHelper,
                                         @Qualifier("springDataRateLimiter")
                                         final RateLimiter rateLimiter,
                                         final RateLimitKeyGenerator rateLimitKeyGenerator,
                                         final RateLimitUtils rateLimitUtils) {
    return new RateLimitFilter(properties,
        routeLocator,
        urlPathHelper,
        rateLimiter,
        rateLimitKeyGenerator,
        rateLimitUtils);
  }
}
