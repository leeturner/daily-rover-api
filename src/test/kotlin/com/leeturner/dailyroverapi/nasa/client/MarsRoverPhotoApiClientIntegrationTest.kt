package com.leeturner.dailyroverapi.nasa.client

import com.leeturner.dailyroverapi.nasa.model.photo.Camera
import com.leeturner.dailyroverapi.nasa.model.photo.NasaPhotoResponse
import com.leeturner.dailyroverapi.nasa.model.photo.Photo
import com.leeturner.dailyroverapi.nasa.model.photo.Rover
import com.leeturner.dailyroverapi.nasa.model.rover.MarsRovers
import com.ninjasquad.springmockk.MockkBean
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.ResponseEntity
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.client.RestTemplate
import strikt.api.expectThat
import strikt.assertions.hasSize
import strikt.assertions.isEmpty
import strikt.assertions.isEqualTo
import java.time.LocalDate

@SpringBootTest
internal class MarsRoverPhotoApiClientIntegrationTest(
    @Autowired private val marsRovers: MarsRovers,
    @Autowired private val marsRoverPhotoApiClient: MarsRoverPhotoApiClient
) {
    @MockkBean
    private lateinit var restTemplate: RestTemplate

    @Test
    internal fun `get photos by earth date loops through all rovers and calls the nasa api`() {
        // let's pick a date where two of the rovers were on Mars
        val earthDate = LocalDate.of(2021, 3,1)
        val camera = Camera(id = 20, name = "FHAZ", roverId = 4, fullName = "Front Hazard Avoidance Camera")

        // perseverance photos
        val perseverance = Rover(
            id = 1,
            name = "perseverance",
            launchDate = LocalDate.of(2020, 7,30),
            landingDate = LocalDate.of(2021, 8,18),
            status = "active"
        )
        val perseverancePhotos = listOf(
            Photo(1, 1, earthDate, "https://nassa.gov/imgs/marz1.jpg", camera, perseverance),
            Photo(2, 2, earthDate, "https://nassa.gov/imgs/marz2.jpg", camera, perseverance),
        )
        every {
            restTemplate.getForEntity(
                "https://api.nasa.gov/mars-photos/api/v1/perseverance?api_key=DEMO_KEY&earth_date=$earthDate",
                NasaPhotoResponse::class.java
            )
        } answers {
            ResponseEntity.ok(NasaPhotoResponse(perseverancePhotos))
        }

        val curiosity = Rover(
            id = 1,
            name = "curiosity",
            launchDate = LocalDate.of(2011, 11,26),
            landingDate = LocalDate.of(2012, 8,6),
            status = "active"
        )
        val curiosityPhotos = listOf(
            Photo(3, 3, earthDate, "https://nassa.gov/imgs/marz3.jpg", camera, curiosity),
        )
        every {
            restTemplate.getForEntity(
                "https://api.nasa.gov/mars-photos/api/v1/curiosity?api_key=DEMO_KEY&earth_date=$earthDate",
                NasaPhotoResponse::class.java
            )
        } answers {
            ResponseEntity.ok(NasaPhotoResponse(curiosityPhotos))
        }

        val nasaPhotoResponse = this.marsRoverPhotoApiClient.getPhotosByEarthDate(earthDate)

        expectThat(nasaPhotoResponse.photos).hasSize(3)

    }

    @Test
    internal fun `get photos for a rover that was not on mars for a specified date does not call the nasa api`() {
        // the first rover in the list should be 'sprint'
        val rover = marsRovers.rovers[0]
        expectThat(rover.name) isEqualTo "sprint"

        // specify an earth date that is 20 days before the rover lands on Mars
        val earthDate = rover.landingDate.minusDays(20)

        val photos = this.marsRoverPhotoApiClient.getPhotosByEarthDateAndMarRover(earthDate, rover)

        expectThat(photos).isEmpty()
        verify(exactly = 0) {
            restTemplate.getForEntity(
                "https://api.nasa.gov/mars-photos/api/v1/perseverance?api_key=DEMO_KEY&earth_date=$earthDate",
                NasaPhotoResponse::class.java
            )
        }
        confirmVerified(restTemplate)
    }

    @Test
    internal fun `get photos for a rover that had ended its mission on the specified earth date does not call the api`() {
        // the first rover in the list should be 'sprint', which has already completed its mission on Mars
        val rover = marsRovers.rovers[0]
        expectThat(rover.name) isEqualTo "sprint"

        // specify an earth date that is 20 days after the max date for the rover on Mars.  max date should
        // not be null here, but we want to know about it if it is
        val earthDate = rover.maxDate!!.plusDays(20)

        val photos = this.marsRoverPhotoApiClient.getPhotosByEarthDateAndMarRover(earthDate, rover)

        expectThat(photos).isEmpty()
        verify(exactly = 0) {
            restTemplate.getForEntity(
                "https://api.nasa.gov/mars-photos/api/v1/perseverance?api_key=DEMO_KEY&earth_date=$earthDate",
                NasaPhotoResponse::class.java
            )
        }
        confirmVerified(restTemplate)
    }

    @Test
    internal fun `get photos uses the correct url for the nasa rover photo api`() {
        val rover = marsRovers.rovers.last()
        expectThat(rover.name) isEqualTo "perseverance"

        // specify an earth date that is 20 days after the rover lands on mars
        val earthDate = rover.landingDate.plusDays(20)

        every {
            restTemplate.getForEntity(
                "https://api.nasa.gov/mars-photos/api/v1/perseverance?api_key=DEMO_KEY&earth_date=$earthDate",
                NasaPhotoResponse::class.java
            )
        } answers {
            ResponseEntity.ok(NasaPhotoResponse(listOf()))
        }

        val photos = this.marsRoverPhotoApiClient.getPhotosByEarthDateAndMarRover(earthDate, rover)

        expectThat(photos).isEmpty()
        verify {
            restTemplate.getForEntity(
                "https://api.nasa.gov/mars-photos/api/v1/perseverance?api_key=DEMO_KEY&earth_date=$earthDate",
                NasaPhotoResponse::class.java
            )
        }
        confirmVerified(restTemplate)
    }

    @Test
    internal fun `when a ResourceAccessException is thrown the request is retried`() {
        // let's pick a date where only one rover was on Mars
        val earthDate = LocalDate.of(2018, 8,6)
        every {
            restTemplate.getForEntity(
                "https://api.nasa.gov/mars-photos/api/v1/curiosity?api_key=DEMO_KEY&earth_date=$earthDate",
                NasaPhotoResponse::class.java
            )
        } throws ResourceAccessException("Cannot access API")

        val nasaPhotoResponse = this.marsRoverPhotoApiClient.getPhotosByEarthDate(earthDate)

        verify(exactly = 2) {
            restTemplate.getForEntity(
                "https://api.nasa.gov/mars-photos/api/v1/curiosity?api_key=DEMO_KEY&earth_date=$earthDate",
                NasaPhotoResponse::class.java
            )
        }
        confirmVerified(restTemplate)
        expectThat(nasaPhotoResponse.photos).isEmpty()
    }
}