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

package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder.reference;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder.ResponseHeaderValueBuilder;
import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.exception.SwaggerContractConverterException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import org.apache.commons.collections.CollectionUtils;

/**
 * Responsible for $ref resolving.
 *
 * @author Sven Bayer
 */
public class SwaggerDefinitionsRefResolverSwagger implements SwaggerReferenceResolver {

	private static final Map<String, String> REPLACEMENTS = Map.of("\\\\r\\\\n",
			System.lineSeparator(), "\\\\n", "\n", "\\\\r", "\r", "\\\\\"", "\"", "\"\\{",
			"{", "\\}\"", "}");

	private final ResponseHeaderValueBuilder responseHeaderValueBuilder = new ResponseHeaderValueBuilder();

	private final String reference;

	public SwaggerDefinitionsRefResolverSwagger(String reference) {
		this.reference = reference;
	}

	/**
	 * Creates a key-value representation for the given reference and Swagger model
	 * definitions.
	 * @param definitions the Swagger model definitions
	 * @return a json representation of the Swagger model definition
	 */
	@Override
	public String resolveReference(final Map<String, Model> definitions) {
		final Object json = resolveDefinitionsRef(this.reference, definitions);

		if (json instanceof String) {
			return String.valueOf(json);
		}
		try {
			final ObjectMapper mapper = new ObjectMapper()
					.enable(SerializationFeature.INDENT_OUTPUT);
			final String jsonString = mapper.writeValueAsString(json);
			final String cleanJson = cleanupJson(jsonString);
			return mapper.writerWithDefaultPrettyPrinter()
					.writeValueAsString(mapper.readTree(cleanJson));
		}
		catch (IOException e) {
			throw new SwaggerContractConverterException("Could not parse jsonMap!", e);
		}
	}

	/**
	 * Cleans up a json-field Json string.
	 * @param jsonString the Json string that got mapped to often by ObjectMapper
	 * @return the cleaned-up Json string
	 */
	private String cleanupJson(String jsonString) {
		for (Map.Entry<String, String> repl : REPLACEMENTS.entrySet()) {
			jsonString = jsonString.replaceAll(repl.getKey(), repl.getValue());
		}
		return jsonString;
	}

	/**
	 * Resolves a Swagger reference with the given Swagger definitions.
	 * @param reference the Swagger reference
	 * @param definitions the Swagger definitions
	 * @return the key-value representation of the Swagger reference
	 */
	private Object resolveDefinitionsRef(final String reference,
			final Map<String, Model> definitions) {
		final String referenceName = reference.substring(reference.lastIndexOf('/') + 1);
		if (definitions == null) {
			throw new SwaggerContractConverterException(
					"Could not resolve reference '" + reference + "'");
		}

		final Model referenceModel = definitions.get(referenceName);
		if (isReferenceValid(referenceModel)) {
			throw new SwaggerContractConverterException(
					"Could not resolve reference '" + reference + "'");
		}

		if (isReferenceEnum(referenceModel)) {
			ModelImpl referenceModelImpl = (ModelImpl) referenceModel;
			return String
					.valueOf(Optional.ofNullable(referenceModelImpl.getDefaultValue())
							.orElse(referenceModelImpl.getEnum().get(0)));
		}
		return definitions.get(referenceName).getProperties().entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey,
						entry -> this.responseHeaderValueBuilder
								.createResponseHeaderValue(entry.getKey(),
										entry.getValue(), definitions)));
	}

	private boolean isReferenceValid(final Model referenceModel) {
		return referenceModel != null && referenceModel.getProperties() == null
				&& (referenceModel instanceof ModelImpl && (CollectionUtils
						.isEmpty(((ModelImpl) referenceModel).getEnum())));
	}

	private boolean isReferenceEnum(final Model referenceModel) {
		return referenceModel.getProperties() == null
				&& referenceModel instanceof ModelImpl;
	}

}
