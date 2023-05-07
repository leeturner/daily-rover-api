package com.leeturner.dailyroverapi.config

import java.time.LocalDate
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
@ConfigurationPropertiesBinding
class LocalDateConverter : Converter<String, LocalDate> {
  override fun convert(timestamp: String): LocalDate {
    return LocalDate.parse(timestamp)
  }
}
