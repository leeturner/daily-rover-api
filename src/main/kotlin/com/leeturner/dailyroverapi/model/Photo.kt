package com.leeturner.dailyroverapi.model

import java.time.LocalDate

data class Photo(
    val id: Int,
    val sol: Int,
    val earthDate: LocalDate,
    val imgSrc: String,
    val camera: Camera,
    val rover: Rover
)
