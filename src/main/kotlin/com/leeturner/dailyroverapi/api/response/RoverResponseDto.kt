package com.leeturner.dailyroverapi.api.response

import java.time.LocalDate

data class RoverResponseDto(
    val id: Int,
    val name: String,
    val landingDate: LocalDate,
    val launchDate: LocalDate,
    val maxDate: LocalDate?,
    val maxSol: Int?,
    val status: String,
)