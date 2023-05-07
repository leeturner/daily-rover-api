package com.leeturner.dailyroverapi.nasa.model.rover

import java.time.LocalDate
import org.springframework.boot.context.properties.bind.ConstructorBinding

data class Rover
@ConstructorBinding
constructor(
    val id: Int,
    val name: String,
    val landingDate: LocalDate,
    val launchDate: LocalDate,
    val maxDate: LocalDate?,
    val maxSol: Int?,
    val status: String,
    val photoApiUrl: String,
)
