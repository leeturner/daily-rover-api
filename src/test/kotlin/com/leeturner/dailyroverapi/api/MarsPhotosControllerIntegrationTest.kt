package com.leeturner.dailyroverapi.api

import com.leeturner.dailyroverapi.TestUtils
import com.leeturner.dailyroverapi.nasa.model.photo.NasaPhotoResponse
import com.leeturner.dailyroverapi.nasa.model.rover.MarsRovers
import com.ninjasquad.springmockk.MockkBean
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.verify
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.ResponseEntity
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.client.RestTemplate
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.time.LocalDate

@SpringBootTest
@AutoConfigureMockMvc
internal class MarsPhotosControllerIntegrationTest(
    @Autowired private val marsRovers: MarsRovers,
    @Autowired private val mockMvc: MockMvc,
) {
    @MockkBean(relaxed = true)
    private lateinit var restTemplate: RestTemplate

    @Test
    internal fun `the default endpoint calls the nasa api for yesterdays date`() {

        val expectedEarthDate = LocalDate.now().minusDays(1)
        this.mockMvc.perform(get("/v1/photos/"))
            .andExpect(status().isOk)
            .andDo(print())

        verify(exactly = 1) {
            restTemplate.getForEntity(
                "https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/photos?api_key=DEMO_KEY&earth_date=$expectedEarthDate",
                NasaPhotoResponse::class.java
            )
        }
        verify(exactly = 1) {
            restTemplate.getForEntity(
                "https://api.nasa.gov/mars-photos/api/v1/rovers/perseverance/photos?api_key=DEMO_KEY&earth_date=$expectedEarthDate",
                NasaPhotoResponse::class.java
            )
        }
        confirmVerified(restTemplate)
    }

    @Test
    internal fun `the get photos endpoint returns the correct payload`() {

        val earthDate = LocalDate.of(2022, 2, 4)
        val nasaPhotoResponse = TestUtils.getNasaResponse("photos-by-earth-date-1.json")

        every {
            restTemplate.getForEntity(
                "https://api.nasa.gov/mars-photos/api/v1/rovers/perseverance/photos?api_key=DEMO_KEY&earth_date=$earthDate",
                NasaPhotoResponse::class.java
            )
        } answers {
            ResponseEntity.ok(nasaPhotoResponse)
        }

        every {
            restTemplate.getForEntity(
                "https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/photos?api_key=DEMO_KEY&earth_date=$earthDate",
                NasaPhotoResponse::class.java
            )
        } answers {
            ResponseEntity.ok(NasaPhotoResponse(earthDate = earthDate, listOf()))
        }

        this.mockMvc.perform(get("/v1/photos/$earthDate"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("\$.earthDate", `is`("2022-02-04")))
            .andExpect(jsonPath("\$.photos.length()", `is`(1)))
            .andExpect(jsonPath("\$.photos[0].id", `is`(nasaPhotoResponse.photos[0].id)))
            .andExpect(jsonPath("\$.photos[0].sol", `is`(nasaPhotoResponse.photos[0].sol)))
            .andExpect(jsonPath("\$.photos[0].earth_date", `is`(nasaPhotoResponse.photos[0].earthDate.toString())))
            .andExpect(jsonPath("\$.photos[0].img_src", `is`(nasaPhotoResponse.photos[0].imgSrc)))

            .andExpect(jsonPath("\$.photos[0].camera.id", `is`(nasaPhotoResponse.photos[0].camera.id)))
            .andExpect(jsonPath("\$.photos[0].camera.name", `is`(nasaPhotoResponse.photos[0].camera.name)))
            .andExpect(jsonPath("\$.photos[0].camera.rover_id", `is`(nasaPhotoResponse.photos[0].camera.roverId)))
            .andExpect(jsonPath("\$.photos[0].camera.full_name", `is`(nasaPhotoResponse.photos[0].camera.fullName)))

            .andExpect(jsonPath("\$.photos[0].rover.id", `is`(nasaPhotoResponse.photos[0].rover.id)))
            .andExpect(jsonPath("\$.photos[0].rover.name", `is`(nasaPhotoResponse.photos[0].rover.name)))
            .andExpect(jsonPath("\$.photos[0].rover.landing_date", `is`(nasaPhotoResponse.photos[0].rover.landingDate.toString())))
            .andExpect(jsonPath("\$.photos[0].rover.launch_date", `is`(nasaPhotoResponse.photos[0].rover.launchDate.toString())))
            .andExpect(jsonPath("\$.photos[0].rover.status", `is`(nasaPhotoResponse.photos[0].rover.status)))
            .andDo(print())

        verify(exactly = 1) {
            restTemplate.getForEntity(
                "https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/photos?api_key=DEMO_KEY&earth_date=$earthDate",
                NasaPhotoResponse::class.java
            )
        }
        verify(exactly = 1) {
            restTemplate.getForEntity(
                "https://api.nasa.gov/mars-photos/api/v1/rovers/perseverance/photos?api_key=DEMO_KEY&earth_date=$earthDate",
                NasaPhotoResponse::class.java
            )
        }
        confirmVerified(restTemplate)
    }

    @Test
    internal fun `get photos for a date where no rovers were on mars does not call the nasa api`() {
        // the first rover in the list should be 'spirit'
        val rover = marsRovers.rovers[0]
        expectThat(rover.name) isEqualTo "spirit"

        // specify an earth date that is 20 days before the rover lands on Mars
        val earthDate = rover.landingDate.minusDays(20)

        this.mockMvc.perform(get("/v1/photos/$earthDate"))
            .andExpect(status().isOk)
            .andDo(print())

        verify(exactly = 0) {
            restTemplate.getForEntity(
                "https://api.nasa.gov/mars-photos/api/v1/rovers/spirit/photos?api_key=DEMO_KEY&earth_date=$earthDate",
                NasaPhotoResponse::class.java
            )
        }
        verify(exactly = 0) {
            restTemplate.getForEntity(
                "https://api.nasa.gov/mars-photos/api/v1/opportunity/photos?api_key=DEMO_KEY&earth_date=$earthDate",
                NasaPhotoResponse::class.java
            )
        }
        verify(exactly = 0) {
            restTemplate.getForEntity(
                "https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/photosapi_key=DEMO_KEY&earth_date=$earthDate",
                NasaPhotoResponse::class.java
            )
        }
        verify(exactly = 0) {
            restTemplate.getForEntity(
                "https://api.nasa.gov/mars-photos/api/v1/rovers/perseverance/photos?api_key=DEMO_KEY&earth_date=$earthDate",
                NasaPhotoResponse::class.java
            )
        }
        confirmVerified(restTemplate)
    }

    @Test
    internal fun `get photos only calls the nasa api for the rovers that are active on mars for the specified date`() {
        // only opportunity and curiosity should have been active on this date.  It was after spirit completed
        // its mission and before perseverance landed
        val earthDate = "2018-06-01"

        this.mockMvc.perform(get("/v1/photos/$earthDate"))
            .andExpect(status().isOk)
            .andDo(print())

        verify(exactly = 0) {
            restTemplate.getForEntity(
                "https://api.nasa.gov/mars-photos/api/v1/rovers/spirit/photos?api_key=DEMO_KEY&earth_date=$earthDate",
                NasaPhotoResponse::class.java
            )
        }
        verify(exactly = 1) {
            restTemplate.getForEntity(
                "https://api.nasa.gov/mars-photos/api/v1/rovers/opportunity/photos?api_key=DEMO_KEY&earth_date=$earthDate",
                NasaPhotoResponse::class.java
            )
        }
        verify(exactly = 1) {
            restTemplate.getForEntity(
                "https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/photos?api_key=DEMO_KEY&earth_date=$earthDate",
                NasaPhotoResponse::class.java
            )
        }
        verify(exactly = 0) {
            restTemplate.getForEntity(
                "https://api.nasa.gov/mars-photos/api/v1/rovers/perseverance/photos?api_key=DEMO_KEY&earth_date=$earthDate",
                NasaPhotoResponse::class.java
            )
        }
        confirmVerified(restTemplate)
    }

}