package ru.kirillspirikhin.mirowidgets.config;

import com.marcosbarbero.cloud.autoconfigure.zuul.ratelimit.config.RateLimitKeyGenerator;
import com.marcosbarbero.cloud.autoconfigure.zuul.ratelimit.config.RateLimitUtils;
import com.marcosbarbero.cloud.autoconfigure.zuul.ratelimit.config.RateLimiter;
import com.marcosbarbero.cloud.autoconfigure.zuul.ratelimit.config.properties.RateLimitProperties;
import com.marcosbarbero.cloud.autoconfigure.zuul.ratelimit.filters.RateLimitPreFilter;
import lombok.Getter;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.web.util.UrlPathHelper;

/**
 * Фильтр для RateLimit.
 */
public class RateLimitFilter extends RateLimitPreFilter {

  /**
   * Свойства.
   */
  @Getter
  private final RateLimitProperties rateLimitProperties;

  /**
   * Конструктор по умолчанию.
   * @param properties properties
   * @param routeLocator routeLocator
   * @param urlPathHelper urlPathHelper
   * @param rateLimiter rateLimiter
   * @param rateLimitKeyGenerator rateLimitKeyGenerator
   * @param rateLimitUtils rateLimitUtils
   */
  public RateLimitFilter(final RateLimitProperties properties,
                         final RouteLocator routeLocator,
                         final UrlPathHelper urlPathHelper,
                         final RateLimiter rateLimiter,
                         final RateLimitKeyGenerator rateLimitKeyGenerator,
                         final RateLimitUtils rateLimitUtils) {
    super(properties,
        routeLocator,
        urlPathHelper,
        rateLimiter,
        rateLimitKeyGenerator,
        rateLimitUtils);
    this.rateLimitProperties = properties;
  }
}
