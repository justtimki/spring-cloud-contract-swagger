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

package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.json;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder.TestFileResourceLoader;
import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder.reference.SwaggerDefinitionsRefResolverSwagger;
import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.exception.SwaggerContractConverterException;
import io.swagger.models.Model;
import io.swagger.models.Swagger;
import io.swagger.parser.SwaggerParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;

/**
 * Test for {@link JsonSchemaComparing}.
 *
 * @author Sven Bayer
 */
public class JsonSchemaComparingTest {

	private JsonSchemaComparing jsonSchemaComparing;

	@Before
	public void init() {
		this.jsonSchemaComparing = new JsonSchemaComparing();
	}

	@DisplayName("Should be true for equal Jsons")
	@Test
	public void equalJsons() throws IOException {
		File jsonFile = TestFileResourceLoader.getResourceAsFile(
				"swagger/jsonFileResolver/withEqualFields/CoffeeRocket.json");
		String json = new String(Files.readAllBytes(jsonFile.toPath()));
		Assertions.assertTrue(this.jsonSchemaComparing.isEquals(json, json),
				"Same json files should be equals!");
	}

	@DisplayName("Should be true for equal Jsons with different values")
	@Test
	public void equalJsonsDifferentValues() throws IOException {
		File expectedJsonFile = TestFileResourceLoader.getResourceAsFile(
				"swagger/jsonFileResolver/withDifferentValues/CoffeeRocket1.json");
		String expectedJson = new String(Files.readAllBytes(expectedJsonFile.toPath()));

		File actualJsonFile = TestFileResourceLoader.getResourceAsFile(
				"swagger/jsonFileResolver/withDifferentValues/CoffeeRocket2.json");
		String actualJson = new String(Files.readAllBytes(actualJsonFile.toPath()));

		Assertions.assertTrue(this.jsonSchemaComparing.isEquals(expectedJson, actualJson),
				"Same json files with differnet values should be equals!");
	}

	@DisplayName("Should be false for different Json files")
	@Test
	public void differentJsonFiles() throws IOException {
		File expectedJsonFile = TestFileResourceLoader.getResourceAsFile(
				"swagger/jsonFileResolver/withDifferentJsons/CoffeeRocket1.json");
		String expectedJson = new String(Files.readAllBytes(expectedJsonFile.toPath()));

		File actualJsonFile = TestFileResourceLoader.getResourceAsFile(
				"swagger/jsonFileResolver/withDifferentJsons/CoffeeRocket2.json");
		String actualJson = new String(Files.readAllBytes(actualJsonFile.toPath()));

		Assertions.assertFalse(
				this.jsonSchemaComparing.isEquals(expectedJson, actualJson),
				"Different Jsons should result in false!");
	}

	@DisplayName("Json file and Swagger definitions should be equal")
	@Test
	public void equalJsonFileAndSwaggerDefinitions() throws IOException {
		File swaggerFile = TestFileResourceLoader.getResourceAsFile(
				"swagger/jsonFileResolver/withEqualFields/external_json_swagger.yml");
		Swagger swagger = new SwaggerParser().read(swaggerFile.getPath());
		Map<String, Model> definitions = swagger.getDefinitions();
		SwaggerDefinitionsRefResolverSwagger swaggerDefinitionsRefResolverSwagger = new SwaggerDefinitionsRefResolverSwagger(
				"#/definitions/CoffeeRocket");
		String expectedJson = swaggerDefinitionsRefResolverSwagger
				.resolveReference(definitions);

		File jsonFile = TestFileResourceLoader.getResourceAsFile(
				"swagger/jsonFileResolver/withEqualFields/CoffeeRocket.json");
		String actualJson = new String(Files.readAllBytes(jsonFile.toPath()));

		Assertions.assertTrue(this.jsonSchemaComparing.isEquals(expectedJson, actualJson),
				"Json from Swagger definitions and Json file should be equal!");
	}

	@DisplayName("Should be not equal for Json with more fields than Swagger definitions")
	@Test
	public void withJsonMoreFields() throws IOException {
		File swaggerFile = TestFileResourceLoader.getResourceAsFile(
				"swagger/jsonFileResolver/withJsonMoreFields/external_json_swagger.yml");
		Swagger swagger = new SwaggerParser().read(swaggerFile.getPath());
		Map<String, Model> definitions = swagger.getDefinitions();
		SwaggerDefinitionsRefResolverSwagger swaggerDefinitionsRefResolverSwagger = new SwaggerDefinitionsRefResolverSwagger(
				"#/definitions/CoffeeRocket");
		String expectedJson = swaggerDefinitionsRefResolverSwagger
				.resolveReference(definitions);

		File jsonFile = TestFileResourceLoader.getResourceAsFile(
				"swagger/jsonFileResolver/withJsonMoreFields/CoffeeRocket.json");
		String actualJson = new String(Files.readAllBytes(jsonFile.toPath()));

		Assertions.assertFalse(
				this.jsonSchemaComparing.isEquals(expectedJson, actualJson),
				"Json file with more fields than Swagger definitions should be false!");
	}

	@DisplayName("Should be not equal for Swagger definitions with more fields than Json")
	@Test
	public void withSwaggerMoreFields() throws IOException {
		File swaggerFile = TestFileResourceLoader.getResourceAsFile(
				"swagger/jsonFileResolver/withSwaggerMoreFields/external_json_swagger.yml");
		Swagger swagger = new SwaggerParser().read(swaggerFile.getPath());
		Map<String, Model> definitions = swagger.getDefinitions();
		SwaggerDefinitionsRefResolverSwagger swaggerDefinitionsRefResolverSwagger = new SwaggerDefinitionsRefResolverSwagger(
				"#/definitions/CoffeeRocket");
		String expectedJson = swaggerDefinitionsRefResolverSwagger
				.resolveReference(definitions);

		File jsonFile = TestFileResourceLoader.getResourceAsFile(
				"swagger/jsonFileResolver/withSwaggerMoreFields/CoffeeRocket.json");
		String actualJson = new String(Files.readAllBytes(jsonFile.toPath()));

		Assertions.assertFalse(
				this.jsonSchemaComparing.isEquals(expectedJson, actualJson),
				"Swagger definitions with more fields than Json file should be false!");
	}

	@DisplayName("Should throw exception for expectedJson null")
	@Test
	public void expectedJsonNull() throws IOException {
		File jsonFile = TestFileResourceLoader.getResourceAsFile(
				"swagger/jsonFileResolver/withEqualFields/CoffeeRocket.json");
		String json = new String(Files.readAllBytes(jsonFile.toPath()));
		SwaggerContractConverterException exception = Assertions.assertThrows(
				SwaggerContractConverterException.class,
				() -> this.jsonSchemaComparing.isEquals(null, json));
		Assertions.assertEquals("JSON of Swagger definitions must not be null!",
				exception.getMessage());
	}

	@DisplayName("Should throw exception for actualJson null")
	@Test
	public void actualJsonNull() throws IOException {
		File jsonFile = TestFileResourceLoader.getResourceAsFile(
				"swagger/jsonFileResolver/withEqualFields/CoffeeRocket.json");
		String json = new String(Files.readAllBytes(jsonFile.toPath()));
		SwaggerContractConverterException exception = Assertions.assertThrows(
				SwaggerContractConverterException.class,
				() -> this.jsonSchemaComparing.isEquals(json, null));
		Assertions.assertEquals("JSON file must not be null!", exception.getMessage());
	}

	@DisplayName("Should throw exception for expected invalid JSON")
	@Test
	public void expectedInvalidJson() throws IOException {
		File jsonFile = TestFileResourceLoader.getResourceAsFile(
				"swagger/jsonFileResolver/withEqualFields/CoffeeRocket.json");
		String json = new String(Files.readAllBytes(jsonFile.toPath()));
		SwaggerContractConverterException exception = Assertions.assertThrows(
				SwaggerContractConverterException.class,
				() -> this.jsonSchemaComparing.isEquals("{ invalid: bla [", json));
		Assertions.assertEquals("Could not parse JSON of Swagger definitions!",
				exception.getMessage());
	}

	@DisplayName("Should throw exception for actual invalid JSON")
	@Test
	public void actualInvalidJson() throws IOException {
		File jsonFile = TestFileResourceLoader.getResourceAsFile(
				"swagger/jsonFileResolver/withEqualFields/CoffeeRocket.json");
		String json = new String(Files.readAllBytes(jsonFile.toPath()));
		SwaggerContractConverterException exception = Assertions.assertThrows(
				SwaggerContractConverterException.class,
				() -> this.jsonSchemaComparing.isEquals(json, "{ invalid: bla ["));
		Assertions.assertEquals("Could not parse JSON of file!", exception.getMessage());
	}

}
