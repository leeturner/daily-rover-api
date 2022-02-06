# daily-rover-api
A Spring Boot Kotlin service to access the NASA mars rover image API by earth date.

You can find out more about the NASA Mars rover image API and get your own API key [here](https://api.nasa.gov) and [here](https://github.com/chrisccerami/mars-photo-api)
## Usage/Examples

This rest API allows you to pass in a date and will return the images for all the Mars rovers for the date you passed.  The date is passed on the url as shown below:

```
GET http://localhost:8080/daily-rover/v1/photos/2015-06-03
```

This will return a `json` payload with all the photos across all the Mars rovers that were active on Mars for that day.  A small example of the payload is below:

```json
{
    "photos": [
        {
            "id": 102685,
            "sol": 1004,
            "camera": {
                "id": 20,
                "name": "FHAZ",
                "rover_id": 5,
                "full_name": "Front Hazard Avoidance Camera"
            },
            "img_src": "http://mars.jpl.nasa.gov/msl-raw-images/proj/msl/redops/ods/surface/sol/01004/opgs/edr/fcam/FLB_486615455EDR_F0481570FHAZ00323M_.JPG",
            "earth_date": "2015-06-03",
            "rover": {
                "id": 5,
                "name": "Curiosity",
                "landing_date": "2012-08-06",
                "launch_date": "2011-11-26",
                "status": "active"
            }
        },
        {
            "id": 102686,
            "sol": 1004,
            "camera": {
                "id": 20,
                "name": "FHAZ",
                "rover_id": 5,
                "full_name": "Front Hazard Avoidance Camera"
            },
            "img_src": "http://mars.jpl.nasa.gov/msl-raw-images/proj/msl/redops/ods/surface/sol/01004/opgs/edr/fcam/FRB_486615455EDR_F0481570FHAZ00323M_.JPG",
            "earth_date": "2015-06-03",
            "rover": {
                "id": 5,
                "name": "Curiosity",
                "landing_date": "2012-08-06",
                "launch_date": "2011-11-26",
                "status": "active"
            }
        },
        {
            "id": 102842,
            "sol": 1004,
            "camera": {
                "id": 21,
                "name": "RHAZ",
                "rover_id": 5,
                "full_name": "Rear Hazard Avoidance Camera"
            },
            "img_src": "http://mars.jpl.nasa.gov/msl-raw-images/proj/msl/redops/ods/surface/sol/01004/opgs/edr/rcam/RLB_486615482EDR_F0481570RHAZ00323M_.JPG",
            "earth_date": "2015-06-03",
            "rover": {
                "id": 5,
                "name": "Curiosity",
                "landing_date": "2012-08-06",
                "launch_date": "2011-11-26",
                "status": "active"
            }
        },
        {
            "id": 102843,
            "sol": 1004,
            "camera": {
                "id": 21,
                "name": "RHAZ",
                "rover_id": 5,
                "full_name": "Rear Hazard Avoidance Camera"
            },
            "img_src": "http://mars.jpl.nasa.gov/msl-raw-images/proj/msl/redops/ods/surface/sol/01004/opgs/edr/rcam/RRB_486615482EDR_F0481570RHAZ00323M_.JPG",
            "earth_date": "2015-06-03",
            "rover": {
                "id": 5,
                "name": "Curiosity",
                "landing_date": "2012-08-06",
                "launch_date": "2011-11-26",
                "status": "active"
            }
        }
    ]
}
```
The payload is often quite large due to the number of photos taken by the rovers - especially the more recent rovers like the [Perseverance](https://mars.nasa.gov/mars2020/)

Along side the photos endpoint there is also a rovers endpoint that returns the internal data the service maintains about the NASA rovers on Mars:

```
GET http://localhost:8080/daily-rover/v1/rovers/
```

This will currently return the following payload:

```json
[
  {
    "id": 1,
    "name": "spirit",
    "landingDate": "2004-01-04",
    "launchDate": "2003-06-10",
    "maxDate": "2010-03-21",
    "maxSol": 2208,
    "status": "complete"
  },
  {
    "id": 3,
    "name": "opportunity",
    "landingDate": "2004-01-25",
    "launchDate": "2003-07-07",
    "maxDate": "2018-06-11",
    "maxSol": 5111,
    "status": "complete"
  },
  {
    "id": 2,
    "name": "curiosity",
    "landingDate": "2012-08-06",
    "launchDate": "2011-11-26",
    "maxDate": null,
    "maxSol": null,
    "status": "active"
  },
  {
    "id": 4,
    "name": "perseverance",
    "landingDate": "2021-02-18",
    "launchDate": "2020-07-30",
    "maxDate": null,
    "maxSol": null,
    "status": "active"
  }
]
```

There are some IntelliJ http request files available in the `/src/test/resources/http` folder if you want to have a play around with the endpoints.
## Tech Stack

This service uses the following languages, frameworks and libraries:

* [Kotlin](https://kotlinlang.org)
* [Spring Boot](https://github.com/spring-projects/spring-boot)
* [Spring Rety](https://github.com/spring-projects/spring-retry)
* [kotlin-logging](https://github.com/MicroUtils/kotlin-logging)

### Testing

* [Strikt](https://strikt.io)
* [mockk](https://github.com/mockk/mockk)
* [springmockk](https://github.com/Ninja-Squad/springmockk)
* [Junit](https://github.com/junit-team/junit5)

## Contributing

Contributions are always welcome!  See the [TODO.md](TODO.md) for some ideas on how you can contribute to this project.

See [CONTRIBUTING.md](CONTRIBUTING.md) for ways to get started.

Please adhere to this project's [Code Of Conduct](CONTRIBUTING.md).

## Authors

- [@leeturner](https://www.github.com/leeturner)