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

package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder;

import java.util.concurrent.atomic.AtomicInteger;

import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.exception.SwaggerContractConverterException;
import io.swagger.models.HttpMethod;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;

/**
 * Test for {@link ContractNameBuilder}.
 *
 * @author Sven Bayer
 */
public class ContractNameBuilderTest {

	private ContractNameBuilder contractNameBuilder;

	@Before
	public void init() {
		this.contractNameBuilder = new ContractNameBuilder();
	}

	@DisplayName("Escapes path for Contract name with path parameter and slashes")
	@Test
	public void escapePathForContractName() {
		String pathName = "/find/planets/{solarSystem}/{system}";
		AtomicInteger priority = new AtomicInteger(1);
		HttpMethod post = HttpMethod.POST;
		String operationId = "findPlanets";
		String contractName = this.contractNameBuilder.createContractName(priority,
				pathName, post, operationId);
		Assertions.assertEquals("1_find_planets_solarSystem_system_POST_findPlanets",
				contractName);
	}

	@DisplayName("Expect Exception for empty Contract path")
	@Test
	public void expectExceptionForEmptyPath() {
		String pathName = "";
		AtomicInteger priority = new AtomicInteger(1);
		HttpMethod post = HttpMethod.POST;
		SwaggerContractConverterException exception = Assertions.assertThrows(
				SwaggerContractConverterException.class, () -> this.contractNameBuilder
						.createContractName(priority, pathName, post, null));
		Assertions.assertEquals("Could not extract path of method from Swagger file: ",
				exception.getMessage());
	}

}
