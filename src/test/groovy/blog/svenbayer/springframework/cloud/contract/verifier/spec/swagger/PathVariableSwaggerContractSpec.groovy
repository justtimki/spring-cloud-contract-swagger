/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger

import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder.TestContractEquals
import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder.util.TestUtils
import org.springframework.cloud.contract.spec.Contract
import org.springframework.cloud.contract.spec.internal.DslProperty
import spock.lang.Specification
import spock.lang.Subject

import java.util.regex.Pattern

class PathVariableSwaggerContractSpec extends Specification {
    @Subject
    SwaggerContractConverter converter = new SwaggerContractConverter()
    TestContractEquals testContractEquals = new TestContractEquals()

    def "should populate path variable to url from x-example"() {
        File swaggerYaml =
                new File(SwaggerContractConverterSpec.getResource("/swagger/pathVariable/pathVariable_swagger.yml").toURI())

        Contract expectedContract = Contract.make {
            name("1_planets_planet_asteroids_asteroidName_limit_limit_GET")
            description("Retrieve existing bean asteroids from a bean planet.")
            priority(1)
            request {
                method(GET())
                urlPath("/coffee-rocket-service/v1.0/planets/Mars/asteroids/Apholo/limit/10") {
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
        when:
        Collection<Contract> contracts = converter.convertFrom(swaggerYaml)
        then:
        testContractEquals.assertContractEquals(Collections.singleton(expectedContract), contracts)
    }
}
