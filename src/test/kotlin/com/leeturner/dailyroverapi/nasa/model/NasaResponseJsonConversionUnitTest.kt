package com.leeturner.dailyroverapi.nasa.model

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.leeturner.dailyroverapi.TestUtils
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.hasSize
import strikt.assertions.isEqualTo
import java.time.LocalDate

internal class NasaResponseJsonConversionUnitTest {

    @Test
    internal fun `a nasa response converts with multiple photos`() {
        val nasaResponse = TestUtils.getNasaResponse("photos-by-earth-date-4.json")

        expectThat(nasaResponse.photos).hasSize(4)
    }

    @Test
    internal fun `a nasa response converts with one photo`() {
        val nasaResponse = TestUtils.getNasaResponse("photos-by-earth-date-1.json")

        expectThat(nasaResponse.photos).hasSize(1)
        val photo = nasaResponse.photos[0]
        expectThat(photo) {
            get { id } isEqualTo 102685
            get { sol } isEqualTo 1004
            get { imgSrc } isEqualTo "http://mars.jpl.nasa.gov/msl-raw-images/proj/msl/redops/ods/surface/sol/01004/opgs/edr/fcam/FLB_486615455EDR_F0481570FHAZ00323M_.JPG"
            get { earthDate } isEqualTo LocalDate.of(2015, 6, 3)

            get { photo.camera.id } isEqualTo 20
            get { photo.camera.name } isEqualTo "FHAZ"
            get { photo.camera.roverId } isEqualTo 5
            get { photo.camera.fullName } isEqualTo "Front Hazard Avoidance Camera"

            get { photo.rover.id } isEqualTo 5
            get { photo.rover.name } isEqualTo "Curiosity"
            get { photo.rover.landingDate } isEqualTo LocalDate.of(2012, 8, 6)
            get { photo.rover.launchDate } isEqualTo LocalDate.of(2011, 11, 26)
            get { photo.rover.status } isEqualTo "active"
        }
    }
}