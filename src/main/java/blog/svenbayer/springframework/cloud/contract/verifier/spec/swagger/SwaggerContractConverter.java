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

package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder.ContractNameBuilder;
import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder.DslValueBuilder;
import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder.RequestBodyParamBuilder;
import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder.ResponseBodyBuilder;
import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder.ResponseHeaderValueBuilder;
import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.valuefields.SwaggerFields;
import groovy.lang.Closure;
import io.swagger.models.HttpMethod;
import io.swagger.models.Operation;
import io.swagger.models.Response;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.AbstractSerializableParameter;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.HeaderParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.parameters.PathParameter;
import io.swagger.models.parameters.QueryParameter;
import io.swagger.parser.SwaggerParser;

import org.springframework.cloud.contract.spec.Contract;
import org.springframework.cloud.contract.spec.ContractConverter;
import org.springframework.cloud.contract.spec.internal.DslProperty;
import org.springframework.cloud.contract.spec.internal.Headers;
import org.springframework.cloud.contract.spec.internal.QueryParameters;
import org.springframework.cloud.contract.spec.internal.Request;

/**
 * Converts a Swagger contract to a Spring Cloud contract.
 *
 * @author Sven Bayer
 */
public final class SwaggerContractConverter implements ContractConverter<Swagger> {

	private static final String TAG_SEP = "_";

	private final ResponseHeaderValueBuilder responseHeaderValueBuilder = new ResponseHeaderValueBuilder();

	private final RequestBodyParamBuilder requestBodyParamBuilder = new RequestBodyParamBuilder();

	private final ResponseBodyBuilder responseBodyBuilder = new ResponseBodyBuilder();

	private final DslValueBuilder dslValueBuilder = new DslValueBuilder();

	private final ContractNameBuilder contractNameBuilder = new ContractNameBuilder();

	/**
	 * Checks if the given file is a Swagger file.
	 * @param file the file to check
	 * @return true if the file is a Swagger file
	 */
	@Override
	public boolean isAccepted(File file) {
		try {
			Swagger swagger = new SwaggerParser().read(file.getPath());
			return swagger != null;
		}
		catch (Exception ignore) {
			return false;
		}
	}

	/**
	 * Converts a Swagger file to Spring Cloud contracts.
	 * @param file the Swagger file
	 * @return the Spring Cloud contracts
	 */
	@Override
	public Collection<Contract> convertFrom(File file) {
		Swagger swagger = new SwaggerParser().read(file.getPath());
		if (swagger == null || swagger.getPaths() == null) {
			return Collections.emptyList();
		}
		SwaggerFileFolder.instance().setPathToSwaggerFile(file.getParentFile().toPath());
		final AtomicInteger priority = new AtomicInteger(1);
		return swagger.getPaths().entrySet().stream().flatMap(pathEntry -> {
			String pathLink = pathEntry.getKey();
			return pathEntry.getValue().getOperationMap().entrySet().stream()
					.map(operationEntry -> createContract(swagger, priority, pathLink,
							operationEntry));
		}).filter(contract -> !contract.isIgnored()).collect(Collectors.toList());
	}

	/**
	 * Creates the Spring Cloud contract for the given path and operation of the Swagger
	 * document.
	 * @param swagger the Swagger document
	 * @param priority the index of the path and operation
	 * @param pathLink the path url
	 * @param operationEntry the operation (GET, POST, PUT, DELETE)
	 * @return the Spring Cloud contract
	 */
	private Contract createContract(Swagger swagger, AtomicInteger priority,
			String pathLink, Map.Entry<HttpMethod, Operation> operationEntry) {
		Contract contract = Contract.make(Closure.IDENTITY);

		createMetaData(priority, pathLink, operationEntry, contract);

		createRequest(swagger, pathLink, operationEntry, contract);

		Operation operation = operationEntry.getValue();
		createResponse(swagger, contract, operation);
		// Async / Callback urls not supported yet async()
		// No support for bodyMatchers
		return contract;
	}

	/**
	 * Sets meta data from a Swagger operation for a Spring Cloud contract, like name,
	 * description, label, priority, ignored.
	 * @param priority the index of the path and operation
	 * @param pathLink the path url
	 * @param operationEntry the operation (GET, POST, PUT, DELETE)
	 * @param contract the Spring Cloud contract to modify
	 */
	private void createMetaData(AtomicInteger priority, String pathLink,
			Map.Entry<HttpMethod, Operation> operationEntry, Contract contract) {
		Operation operation = operationEntry.getValue();

		String contractName = this.contractNameBuilder.createContractName(priority,
				pathLink, operationEntry.getKey());
		contract.setName(contractName);

		if (operation.getDescription() != null) {
			contract.description(operation.getDescription());
		}
		if (operation.getTags() != null) {
			contract.setLabel(String.join(TAG_SEP, operation.getTags()));
		}

		contract.setPriority(priority.getAndIncrement());
		contract.setIgnored(operation.getVendorExtensions() != null
				&& operation.getVendorExtensions()
						.get(SwaggerFields.X_IGNORE.field()) != null
				&& (Boolean) operation.getVendorExtensions()
						.get(SwaggerFields.X_IGNORE.field()));
	}

	/**
	 * Sets the response data for the Spring Cloud contract for the given operation.
	 * @param swagger the Swagger document
	 * @param contract the Spring Cloud contract
	 * @param operation the operation (GET, POST, PUT, DELETE)
	 */
	private void createResponse(Swagger swagger, Contract contract, Operation operation) {
		org.springframework.cloud.contract.spec.internal.Response response = new org.springframework.cloud.contract.spec.internal.Response();
		contract.setResponse(response);

		Map.Entry<String, Response> responseEntry = operation.getResponses().entrySet()
				.iterator().next();
		String responseStatus = responseEntry.getKey();
		response.status(Integer.parseInt(responseStatus));

		response.headers(Closure.IDENTITY);
		Headers responseHeaders = response.getHeaders();

		if (responseEntry.getValue().getHeaders() != null) {
			responseEntry.getValue().getHeaders().forEach((key, value) -> {
				if (key != null) {
					DslProperty serverValue = this.responseHeaderValueBuilder
							.createDslResponseHeaderValue(key, value,
									swagger.getDefinitions());
					responseHeaders.header(key, serverValue);
				}
			});
		}
		if (operation.getProduces() != null) {
			operation.getProduces().forEach(contentType -> {
				if (contentType.equals("*/*")) {
					responseHeaders.contentType("*/*");
				}
				else {
					responseHeaders.contentType(contentType);
				}
			});
		}

		// Cookie parameters are not supported by Swagger 2.0 ?
		if (responseEntry.getValue().getResponseSchema() != null) {
			String bodyValue = this.responseBodyBuilder.createValueForResponseBody(
					responseEntry.getValue(), swagger.getDefinitions());
			response.body(bodyValue);
		}
	}

	/**
	 * Sets the request data for the given operation.entry.
	 * @param swagger the Swagger document
	 * @param pathLink the path url
	 * @param operationEntry the operation (GET, PUT, POST, DELETE)
	 * @param contract the Spring Cloud contract
	 */
	private void createRequest(Swagger swagger, String pathLink,
			Map.Entry<HttpMethod, Operation> operationEntry, Contract contract) {
		Operation operation = operationEntry.getValue();
		final Request request = new Request();
		contract.setRequest(request);

		HttpMethod httpMethod = operationEntry.getKey();
		if (httpMethod != null) {
			request.method(httpMethod.name());
		}
		if (pathLink != null) {
			request.urlPath(swagger.getBasePath() + pathLink);
			if (operation.getParameters() != null) {
				operation.getParameters().stream()
						.filter(param -> param instanceof PathParameter)
						.map(PathParameter.class::cast)
						.forEach(param -> request
								.urlPath(request.getUrlPath().getClientValue().toString()
										.replace("{" + param.getName() + "}",
												extractExample(param))));

				final QueryParameters queryParameters = new QueryParameters();
				request.getUrlPath().setQueryParameters(queryParameters);
				operation.getParameters().stream()
						.filter(param -> param instanceof QueryParameter)
						.map(AbstractSerializableParameter.class::cast).forEach(param -> {
							DslProperty<Object> value = this.dslValueBuilder
									.createDslValueForParameter(param);
							if (value != null) {
								queryParameters.parameter(param.getName(), value);
							}
						});
			}
		}

		createRequestHeaders(swagger, operation, request);
	}

	private String extractExample(final PathParameter parameter) {
		return Optional.ofNullable(parameter.getVendorExtensions().get("x-example"))
				.map(String.class::cast).orElse(parameter.getName());
	}

	/**
	 * Creates headers for the request.
	 * @param swagger the Swagger document
	 * @param operation the operation (GET, PUT, POST, DELETE)
	 * @param request the contract request
	 */
	private void createRequestHeaders(Swagger swagger, Operation operation,
			Request request) {
		request.headers(Closure.IDENTITY);
		Headers requestHeaders = request.getHeaders();

		if (operation.getParameters() != null) {
			operation.getParameters()
					.forEach(param -> createRequestHeaderBodyParameters(swagger, request,
							requestHeaders, param));
		}
		if (operation.getConsumes() != null) {
			operation.getConsumes().forEach(contentType -> {
				if (contentType.equals("*/*")) {
					requestHeaders.contentType("");
				}
				else {
					requestHeaders.contentType(contentType);
				}
			});
		}
	}

	/**
	 * Create the parameters for request header and body.
	 * @param swagger the Swagger document
	 * @param request the contract request
	 * @param requestHeaders the contract headers
	 * @param param the Swagger parameters
	 */
	private void createRequestHeaderBodyParameters(Swagger swagger, Request request,
			Headers requestHeaders, Parameter param) {
		if (param instanceof HeaderParameter) {
			HeaderParameter headerParameter = (HeaderParameter) param;
			DslProperty clientValue = this.dslValueBuilder
					.createDslValueForParameter(headerParameter);
			if (clientValue != null && headerParameter.getName() != null) {
				requestHeaders.header(headerParameter.getName(), clientValue);
			}
		}
		// Cookie parameters are not supported by Swagger 2.0
		if (param instanceof BodyParameter) {
			BodyParameter bodyParameter = (BodyParameter) param;
			String value = this.requestBodyParamBuilder
					.createValueForRequestBody(bodyParameter, swagger.getDefinitions());
			if (value != null) {
				request.body(value);
			}
		}
	}

	/**
	 * This is not supported!
	 * @param contract the contract that will not be converted
	 * @return an empty Swagger document
	 */
	@Override
	public Swagger convertTo(Collection<Contract> contract) {
		return new Swagger();
	}

}
