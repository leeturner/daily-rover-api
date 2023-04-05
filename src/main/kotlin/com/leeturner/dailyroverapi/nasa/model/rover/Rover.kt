package com.leeturner.dailyroverapi.nasa.model.rover

import org.springframework.boot.context.properties.bind.ConstructorBinding
import java.time.LocalDate

data class Rover @ConstructorBinding constructor(
    val id: Int,
    val name: String,
    val landingDate: LocalDate,
    val launchDate: LocalDate,
    val maxDate: LocalDate?,
    val maxSol: Int?,
    val status: String,
    val photoApiUrl: String,
)