package com.leeturner.dailyroverapi.api

import com.leeturner.dailyroverapi.nasa.model.rover.Rover
import com.leeturner.dailyroverapi.nasa.model.rover.MarsRovers
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class RoversApiController(
    private val marsRovers: MarsRovers
) {
    @GetMapping("/rovers")
    fun getRovers(): List<Rover> {
        return this.marsRovers.rovers
    }
}