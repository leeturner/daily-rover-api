package com.leeturner.dailyroverapi.nasa.model.rover

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import strikt.api.expectThat
import strikt.assertions.hasSize
import strikt.assertions.isEqualTo
import java.time.LocalDate

@SpringBootTest
internal class MarsRoversIntegrationTest(
    @Autowired private val marsRovers: MarsRovers
){
    @Test
    internal fun `there are the correct number of rovers in the config`() {
        expectThat(this.marsRovers.rovers).hasSize(4)
    }

    @Test
    internal fun `ensure there the correct properties are set for each rover`() {
        expectThat(this.marsRovers.rovers[0]) {
            get { id } isEqualTo 1
            get { name } isEqualTo "spirit"
            get { launchDate } isEqualTo LocalDate.of(2003,6,10)
            get { landingDate } isEqualTo LocalDate.of(2004,1,4)
            get { maxDate } isEqualTo LocalDate.of(2010,3,21)
            get { maxSol } isEqualTo 2208
            get { status } isEqualTo "complete"
            get { photoApiUrl } isEqualTo "https://api.nasa.gov/mars-photos/api/v1/rovers/spirit/photos?api_key=DEMO_KEY"
        }

        expectThat(this.marsRovers.rovers[1]) {
            get { id } isEqualTo 3
            get { name } isEqualTo "opportunity"
            get { launchDate } isEqualTo LocalDate.of(2003,7,7)
            get { landingDate } isEqualTo LocalDate.of(2004,1,25)
            get { maxDate } isEqualTo LocalDate.of(2018,6,11)
            get { maxSol } isEqualTo 5111
            get { status } isEqualTo "complete"
            get { photoApiUrl } isEqualTo "https://api.nasa.gov/mars-photos/api/v1/rovers/opportunity/photos?api_key=DEMO_KEY"
        }

        expectThat(this.marsRovers.rovers[2]) {
            get { id } isEqualTo 2
            get { name } isEqualTo "curiosity"
            get { launchDate } isEqualTo LocalDate.of(2011,11, 26)
            get { landingDate } isEqualTo LocalDate.of(2012,8,6)
            get { maxDate } isEqualTo null
            get { maxSol } isEqualTo null
            get { status } isEqualTo "active"
            get { photoApiUrl } isEqualTo "https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/photos?api_key=DEMO_KEY"
        }

        expectThat(this.marsRovers.rovers[3]) {
            get { id } isEqualTo 4
            get { name } isEqualTo "perseverance"
            get { launchDate } isEqualTo LocalDate.of(2020,7, 30)
            get { landingDate } isEqualTo LocalDate.of(2021,2,18)
            get { maxDate } isEqualTo null
            get { maxSol } isEqualTo null
            get { status } isEqualTo "active"
            get { photoApiUrl } isEqualTo "https://api.nasa.gov/mars-photos/api/v1/rovers/perseverance/photos?api_key=DEMO_KEY"
        }
    }
}