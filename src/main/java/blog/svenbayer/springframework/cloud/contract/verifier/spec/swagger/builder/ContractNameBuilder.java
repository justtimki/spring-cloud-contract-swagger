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

import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.exception.SwaggerContractConverterException;
import io.swagger.models.HttpMethod;
import org.springframework.util.StringUtils;

/**
 * Builds a contract name for a given path and http method.
 *
 * @author Sven Bayer
 */
public class ContractNameBuilder {

	private static final String PATH_SEP = "_";

	/**
	 * Extract the path without the leading slash and without the last closing
	 * curly-brace.
	 */
	private static final String PATH_EXTRACT = "([^\\/].*[^\\}])";

	/**
	 * Replace all curly-braces and slashes.
	 */
	private static final String PATH_CLEANUP = "(\\}\\/\\{|\\/\\{|\\}\\/|\\}|\\/)";

	/**
	 * Creates a contract name for a given path and http method.
	 * @param priority the order of the method
	 * @param pathLink the path of the endpoint
	 * @param httpMethod the operation (GET, POST, PUT, DELETE)
	 * @param operationId the unique operation id
	 * @return the formatted contract name
	 */
	public String createContractName(AtomicInteger priority, String pathLink,
			HttpMethod httpMethod, @Nullable String operationId) {
		Matcher pathMatcher = Pattern.compile(PATH_EXTRACT).matcher(pathLink);
		if (!pathMatcher.find()) {
			throw new SwaggerContractConverterException(
					"Could not extract path of method from Swagger file: " + pathLink);
		}
		String extractedPath = pathMatcher.group(1);
		String cleanedUpPathLink = extractedPath.replaceAll(PATH_CLEANUP, PATH_SEP);
		if (StringUtils.isEmpty(operationId)) {
			return priority + PATH_SEP + cleanedUpPathLink + PATH_SEP + httpMethod.name();
		}
		return priority + PATH_SEP + cleanedUpPathLink + PATH_SEP + httpMethod.name()
				+ PATH_SEP + operationId;
	}

}
