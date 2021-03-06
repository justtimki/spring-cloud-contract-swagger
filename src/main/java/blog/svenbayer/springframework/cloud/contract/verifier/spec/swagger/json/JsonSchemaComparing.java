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

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.exception.SwaggerContractConverterException;
import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.json.model.JsonKeyValuePair;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ValueNode;

/**
 * Compares two Jsons if they have the same schema.
 *
 * @author Sven Bayer
 */
public class JsonSchemaComparing {

	private final ObjectMapper mapper = new ObjectMapper();

	/**
	 * Compares two Jsons if they have the same schema. Does this not already exist? Are
	 * we re-inventing the wheel???
	 * @param expectedJson the expected Json
	 * @param actualJson the actual Json
	 * @return true if the Json schemas are equal
	 */
	public boolean isEquals(String expectedJson, String actualJson) {
		JsonNode expectedNode;
		JsonNode actualNode;
		if (expectedJson == null) {
			throw new SwaggerContractConverterException(
					"JSON of Swagger definitions must not be null!");
		}
		if (actualJson == null) {
			throw new SwaggerContractConverterException("JSON file must not be null!");
		}
		try {
			expectedNode = this.mapper.readTree(expectedJson);
		}
		catch (IOException e) {
			throw new SwaggerContractConverterException(
					"Could not parse JSON of Swagger definitions!", e);
		}
		try {
			actualNode = this.mapper.readTree(actualJson);
		}
		catch (IOException e) {
			throw new SwaggerContractConverterException("Could not parse JSON of file!",
					e);
		}
		return isEquals(expectedNode, actualNode);
	}

	/**
	 * Compares two JsonNodes if their schema is equal.
	 * @param expectedJsonSchema the expected JsonNode
	 * @param actualJsonSchema the actual JsonNode
	 * @return true if the schema of both JsonNodes is equal
	 */
	private boolean isEquals(JsonNode expectedJsonSchema, JsonNode actualJsonSchema) {
		Set<JsonKeyValuePair> expectedJsonMap = mapNode("root", expectedJsonSchema);
		Set<JsonKeyValuePair> actualJsonMap = mapNode("root", actualJsonSchema);
		return expectedJsonMap.containsAll(actualJsonMap)
				&& actualJsonMap.containsAll(expectedJsonMap);
	}

	/**
	 * Maps a JsonNode to a key-value representation that ignores values.
	 * @param name the name of the key
	 * @param node the Json node value
	 * @return the mapped key-value Set of the key and Json node value
	 */
	private Set<JsonKeyValuePair> mapNode(String name, JsonNode node) {
		Set<JsonKeyValuePair> elements = new HashSet<>();
		Iterator<Map.Entry<String, JsonNode>> iteratorFields = node.fields();
		if (!iteratorFields.hasNext()) {
			Iterator<JsonNode> iteratorElements = node.elements();
			while (iteratorElements.hasNext()) {
				JsonNode next = iteratorElements.next();
				if (!(next instanceof ValueNode)) {
					elements.add(new JsonKeyValuePair(name, mapNode(name, next)));
				}
			}
		}
		while (iteratorFields.hasNext()) {
			Map.Entry<String, JsonNode> next = iteratorFields.next();
			JsonNode value = next.getValue();
			if (value instanceof ValueNode) {
				Set<JsonKeyValuePair> subKeyAsValue = new HashSet<>();
				subKeyAsValue.add(new JsonKeyValuePair(next.getKey(), null));
				elements.add(new JsonKeyValuePair(name, subKeyAsValue));
			}
			else {
				Set<JsonKeyValuePair> subValueForKey = mapNode(next.getKey(), value);
				elements.add(new JsonKeyValuePair(name, subValueForKey));
			}
		}
		return elements;
	}

}
