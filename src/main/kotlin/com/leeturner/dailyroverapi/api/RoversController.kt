package com.leeturner.dailyroverapi.api

import com.leeturner.dailyroverapi.nasa.model.rover.Rover
import com.leeturner.dailyroverapi.nasa.model.rover.MarsRovers
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController("v1/rovers")
class RoversController(
    private val marsRovers: MarsRovers
) {
    @GetMapping("/")
    fun getRovers(): List<Rover> {
        return this.marsRovers.rovers
    }
}