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

package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.exception;

/**
 * Exception for Swagger Contract Converter related errors.
 *
 * @author Sven Bayer
 */
public class SwaggerContractConverterException extends RuntimeException {

	/**
	 * Swagger Contract Converter Exception with message.
	 * @param message the error message
	 */
	public SwaggerContractConverterException(final String message) {
		super(message);
	}

	/**
	 * Swagger Contract Converter Exception with message and cause.
	 * @param message the error message
	 * @param cause the cause
	 */
	public SwaggerContractConverterException(final String message,
			final Throwable cause) {
		super(message, cause);
	}

}
