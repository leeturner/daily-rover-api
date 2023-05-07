package com.leeturner.dailyroverapi.config

import java.time.Duration
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class ApplicationConfig {
  @Bean
  fun restTemplate(restTemplateBuilder: RestTemplateBuilder): RestTemplate {
    return restTemplateBuilder
        .setConnectTimeout(Duration.ofSeconds(5))
        .setReadTimeout(Duration.ofSeconds(5))
        .build()
  }
}
