package com.leeturner.dailyroverapi.config

import org.springframework.boot.context.properties.ConfigurationPropertiesBinding
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
@ConfigurationPropertiesBinding
class LocalDateConverter : Converter<String, LocalDate> {
    override fun convert(timestamp: String): LocalDate {
        return LocalDate.parse(timestamp)
    }
}
