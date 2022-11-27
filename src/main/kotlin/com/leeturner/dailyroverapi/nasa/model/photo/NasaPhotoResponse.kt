package com.leeturner.dailyroverapi.nasa.model.photo

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import java.time.LocalDate

data class NasaPhotoResponse(
    @JsonDeserialize(using = LocalDateDeserializer::class)
    val earthDate: LocalDate,
    val photos: List<Photo> = listOf()
)
