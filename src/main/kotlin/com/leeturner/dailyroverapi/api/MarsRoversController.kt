package com.leeturner.dailyroverapi.api

import com.leeturner.dailyroverapi.api.response.RoverResponseDto
import com.leeturner.dailyroverapi.nasa.model.rover.MarsRovers
import com.leeturner.dailyroverapi.nasa.model.rover.Rover
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("v1/rovers")
class MarsRoversController(private val marsRovers: MarsRovers) {
  @GetMapping("/", produces = [MediaType.APPLICATION_JSON_VALUE])
  fun getRovers(): List<RoverResponseDto> {
    return this.marsRovers.rovers.map { this.convertToDto(it) }
  }

  fun convertToDto(rover: Rover): RoverResponseDto {
    return RoverResponseDto(
        id = rover.id,
        name = rover.name,
        landingDate = rover.landingDate,
        launchDate = rover.launchDate,
        maxDate = rover.maxDate,
        maxSol = rover.maxSol,
        status = rover.status,
    )
  }
}
