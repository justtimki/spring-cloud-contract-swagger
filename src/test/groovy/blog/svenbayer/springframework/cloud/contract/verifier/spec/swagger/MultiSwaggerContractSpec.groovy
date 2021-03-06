package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger

import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder.TestContractEquals
import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder.util.TestUtils
import org.springframework.cloud.contract.spec.Contract
import org.springframework.cloud.contract.spec.internal.DslProperty
import spock.lang.Specification
import spock.lang.Subject

import java.util.regex.Pattern

/**
 * @author Sven Bayer
 */
class MultiSwaggerContractSpec extends Specification {

    @Subject
    SwaggerContractConverter converter = new SwaggerContractConverter()
    TestContractEquals testContractEquals = new TestContractEquals()

    def "should convert from multiple swagger to contract"() {
        given:
        File multipleSwaggerYaml = new File(SwaggerContractConverterSpec.getResource("/swagger/multiple/multiple_swagger.yml").toURI())
        Contract expectedContract0 = Contract.make {
            label("takeoff_coffee_bean_rocket")
            name("1_takeoff_POST_takeoff")
            description("API endpoint to send a coffee rocket to a bean planet and returns the bean planet.")
            priority(1)
            request {
                method(POST())
                urlPath("/coffee-rocket-service/v1.0/takeoff") {
                    queryParameters {
                        parameter("withWormhole", new DslProperty(Pattern.compile("(true|false)"), true))
                        parameter("viaHyperLoop", new DslProperty(Pattern.compile("(true|false)"), true))
                    }
                }
                headers {
                    header("X-Request-ID", new DslProperty(Pattern.compile(".+"), "123456"))
                    header(TestUtils.REQUEST_TYPE_HEADER_NAME, new DslProperty(Pattern.compile(".+"), "CoffeeRocket"))
                    contentType(applicationJson())
                }
                body("""{
  "beanonauts" : [ {
    "name" : "name",
    "age" : 1
  } ],
  "boxes" : [ "boxes" ],
  "fuel" : 1.1,
  "weight" : 1.1,
  "itinerary" : {
    "destination" : "destination",
    "departure" : "departure"
  },
  "rocketName" : "rocketName"
}""")
            }
            response {
                status(201)
                headers {
                    header("X-RateLimit-Limit", 1)
                    header(TestUtils.RESPONSE_TYPE_HEADER_NAME, "BeanPlanet")
                    contentType(allValue())
                }
                body("""{
  "asteroids" : [ {
    "shape" : "ROUND",
    "aliens" : [ {
      "heads" : [ "heads" ]
    } ],
    "name" : "name",
    "speed" : 1,
    "istransparent" : true
  } ],
  "size" : 1,
  "name" : "name"
}""")
            }
        }
        Contract expectedContract1 = Contract.make {
            label("land")
            name("3_land_POST")
            description("Lands a coffee rocket on a bean planet and returns the coffee rocket.")
            priority(3)
            request {
                method(POST())
                urlPath("/coffee-rocket-service/v1.0/land") {
                    queryParameters {
                    }
                }
                headers {
                    header("X-Request-ID", new DslProperty(Pattern.compile(".+"), "X-Request-ID"))
                    header(TestUtils.REQUEST_TYPE_HEADER_NAME, new DslProperty(Pattern.compile(".+"), "BeanPlanet"))
                    contentType(applicationJson())
                }
                body("""{
  "asteroids" : [ {
    "shape" : "ROUND",
    "aliens" : [ {
      "heads" : [ "heads" ]
    } ],
    "name" : "name",
    "speed" : 1,
    "istransparent" : true
  } ],
  "size" : 1,
  "name" : "name"
}""")
            }
            response {
                status(201)
                headers {
                    header(TestUtils.RESPONSE_TYPE_HEADER_NAME, "CoffeeRocket")
                    contentType(applicationJson())
                }
                body("""{
  "beanonauts" : [ {
    "name" : "name",
    "age" : 1
  } ],
  "boxes" : [ "boxes" ],
  "fuel" : 1.1,
  "weight" : 1.1,
  "itinerary" : {
    "destination" : "destination",
    "departure" : "departure"
  },
  "rocketName" : "rocketName"
}""")
            }
        }
        Contract expectedContract2 = Contract.make {
            name("4_find_planets_solarSystem_GET")
            description("Find planets in the given Solar System.")
            priority(4)
            request {
                method(GET())
                urlPath("/coffee-rocket-service/v1.0/find/planets/Sol") {
                    queryParameters {
                        parameter("planetName", new DslProperty(Pattern.compile(".+"), "planetName"))
                        parameter("numAsteroids", new DslProperty(Pattern.compile("[0-9]+"), 1))
                        parameter("minSize", new DslProperty(Pattern.compile("[0-9]+"), 1))
                    }
                }
                headers {
                    header("X-Request-ID", new DslProperty(Pattern.compile(".+"), "X-Request-ID"))
                    contentType(applicationJson())
                }
            }
            response {
                status(200)
                headers {
                    header(TestUtils.RESPONSE_TYPE_HEADER_NAME, "BeanPlanet")
                    contentType(allValue())
                }
                body("""{
  "asteroids" : [ {
    "shape" : "ROUND",
    "aliens" : [ {
      "heads" : [ "heads" ]
    } ],
    "name" : "name",
    "speed" : 1,
    "istransparent" : true
  } ],
  "size" : 1,
  "name" : "name"
}""")
            }
        }
        Contract expectedContract3 = Contract.make {
            name("5_planets_planet_asteroids_asteroidName_GET")
            description("Retrieve existing bean asteroids from a bean planet.")
            priority(5)
            request {
                method(GET())
                urlPath("/coffee-rocket-service/v1.0/planets/Mars/asteroids/Apholo") {
                    queryParameters {
                    }
                }
                headers {
                    header("X-Request-ID", new DslProperty(Pattern.compile(".+"), "X-Request-ID"))
                }
            }
            response {
                status(200)
                headers {
                    header(TestUtils.RESPONSE_TYPE_HEADER_NAME, "BeanAsteroid")
                    contentType(allValue())
                }
                body(
                        """{
  "shape" : "ROUND",
  "name" : "name",
  "speed" : 1,
  "aliens" : [ {
      "heads" : [ "heads" ]
    } ],
  "istransparent": true
}""")
            }
        }
        Contract expectedContract4 = Contract.make {
            name("6_planets_planet_asteroids_asteroidName_PUT")
            description("Updates an existing bean asteroids of a bean planet.")
            priority(6)
            request {
                method(PUT())
                urlPath("/coffee-rocket-service/v1.0/planets/Mars/asteroids/Apholo") {
                    queryParameters {
                    }
                }
                headers {
                    header("X-Request-ID", new DslProperty(Pattern.compile(".+"), "X-Request-ID"))
                    header(TestUtils.REQUEST_TYPE_HEADER_NAME, new DslProperty(Pattern.compile(".+"), "BeanAsteroid"))
                    contentType(applicationJson())
                }
                body(
                        """{
  "shape" : "ROUND",
  "name" : "name",
  "speed" : 1,
  "aliens" : [ {
      "heads" : [ "heads" ]
    } ],
  "istransparent": true
}""")
            }
            response {
                status(200)
                headers {
                    header(TestUtils.RESPONSE_TYPE_HEADER_NAME, "BeanSpaceLocation")
                    contentType(allValue())
                }
                body(
                        """{
  "x" : 1,
  "y" : 1
}""")
            }
        }
        Contract expectedContract5 = Contract.make {
            name("7_planets_planet_asteroids_asteroidName_POST")
            description("Adds a bean asteroids to a bean planet.")
            priority(7)
            request {
                method(POST())
                urlPath("/coffee-rocket-service/v1.0/planets/Mars/asteroids/Apholo") {
                    queryParameters {
                    }
                }
                headers {
                    header("X-Request-ID", new DslProperty(Pattern.compile(".+"), "X-Request-ID"))
                    header(TestUtils.REQUEST_TYPE_HEADER_NAME, new DslProperty(Pattern.compile(".+"), "BeanAsteroid"))
                    contentType(applicationJson())
                }
                body(
                        """{
  "shape" : "ROUND",
  "name" : "name",
  "speed" : 1,
  "aliens" : [ {
      "heads" : [ "heads" ]
    } ],
  "istransparent": true
}""")
            }
            response {
                status(200)
                headers {
                    header(TestUtils.RESPONSE_TYPE_HEADER_NAME, "BeanSpaceLocation")
                    contentType(allValue())
                }
                body(
                        """{
  "x" : 1,
  "y" : 1
}""")
            }
        }
        Contract expectedContract6 = Contract.make {
            name("8_planets_planet_asteroids_asteroidName_DELETE")
            description("Removes an existing bean asteroids from a bean planet.")
            priority(8)
            request {
                method(DELETE())
                urlPath("/coffee-rocket-service/v1.0/planets/Mars/asteroids/Apholo") {
                    queryParameters {
                    }
                }
                headers {
                    header("X-Request-ID", new DslProperty(Pattern.compile(".+"), "X-Request-ID"))
                }
            }
            response {
                status(204)
                headers {
                    contentType(allValue())
                }
            }
        }
        Contract expectedContract7 = Contract.make {
            name("9_planets_names_GET")
            description("Find planets names in the given Solar System.")
            priority(9)
            label("get planet names")
            request {
                method(GET())
                urlPath("/coffee-rocket-service/v1.0/planets/names") {
                    queryParameters {}
                }
                headers {
                    header("X-Request-ID", new DslProperty(Pattern.compile(".+"), "X-Request-ID"))
                    contentType(applicationJson())
                }
            }
            response {
                status(200)
                headers {
                    contentType(allValue())
                }
                body("""{ "items: [{"x" : 1,"y" : 1}]""")
            }
        }
        when:
        Collection<Contract> contracts = converter.convertFrom(multipleSwaggerYaml)
        then:
        testContractEquals.assertContractEquals([expectedContract0, expectedContract1, expectedContract2, expectedContract3, expectedContract4, expectedContract5, expectedContract6, expectedContract7], contracts)
    }
}
