package com.leeturner.dailyroverapi.nasa.model.photo

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import java.time.LocalDate

data class Photo(
    val id: Int,
    val sol: Int,
    @JsonProperty("earth_date")
    @JsonDeserialize(using = LocalDateDeserializer::class)
    val earthDate: LocalDate,
    @JsonProperty("img_src") val imgSrc: String,
    val camera: Camera,
    val rover: Rover
)
