package com.leeturner.dailyroverapi.nasa.model.photo

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import java.time.LocalDate

data class Rover(
    val id: Int,
    val name: String,
    @JsonProperty("landing_date")
    @JsonDeserialize(using = LocalDateDeserializer::class)
    val landingDate: LocalDate,
    @JsonProperty("launch_date")
    @JsonDeserialize(using = LocalDateDeserializer::class)
    val launchDate: LocalDate,
    val status: String
)
