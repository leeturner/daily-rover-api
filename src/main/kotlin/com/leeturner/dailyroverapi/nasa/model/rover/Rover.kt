package com.leeturner.dailyroverapi.nasa.model.rover

import org.springframework.boot.context.properties.ConstructorBinding
import java.time.LocalDate

@ConstructorBinding
data class Rover(
    val id: Int,
    val name: String,
    val landingDate: LocalDate,
    val launchDate: LocalDate,
    val maxDate: LocalDate?,
    val maxSol: Int?,
    val status: String,
    val photoApiUrl: String,
)