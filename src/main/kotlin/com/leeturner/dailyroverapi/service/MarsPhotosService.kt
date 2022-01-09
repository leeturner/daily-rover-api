package com.leeturner.dailyroverapi.service

import com.leeturner.dailyroverapi.nasa.client.MarsRoverPhotoApiClient
import com.leeturner.dailyroverapi.nasa.model.photo.NasaPhotoResponse
import com.leeturner.dailyroverapi.nasa.model.photo.Photo
import com.leeturner.dailyroverapi.nasa.model.rover.MarsRovers
import com.leeturner.dailyroverapi.nasa.model.rover.Rover
import com.leeturner.dailyroverapi.nasa.model.rover.RoverStatus
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.time.LocalDate

private val logger = KotlinLogging.logger {}

@Service
class MarsPhotosService(
    val marsRovers: MarsRovers,
    val marsRoverPhotoApiClient: MarsRoverPhotoApiClient,
) {
    fun getMarsPhotosByEarthDate(earthDate: LocalDate): NasaPhotoResponse {
        val photoList: MutableList<Photo> = mutableListOf()
        this.marsRovers.rovers.forEach { rover ->
            photoList += this.getPhotosByEarthDateAndMarRover(earthDate, rover)
        }
        return NasaPhotoResponse(photos = photoList)
    }

    fun getPhotosByEarthDateAndMarRover(earthDate: LocalDate, rover: Rover): List<Photo> {
        // first lets check to see if the specified rover was on Mars for the specified date
        if (earthDate < rover.landingDate) {
            logger.info { "Mars Rover (${rover.name}) was not on Mars for the specified date - $earthDate. No API call needs to be made" }
            return listOf()
        }

        // now lets check to see if the rover's mission had ended for the specified date
        rover.maxDate?.let {
            if (earthDate > rover.maxDate && rover.status == RoverStatus.COMPETE.status) {
                logger.info { "Mars Rover (${rover.name}) had ended its mission on Mars for the specified date - $earthDate. No API call needs to be made" }
                return listOf()
            }
        }

        return this.marsRoverPhotoApiClient.getRoverPhotosByDate(earthDate, rover)
    }
}