package com.leeturner.dailyroverapi.nasa.model.rover

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "nasa")
data class MarsRovers(val rovers: List<Rover>)
