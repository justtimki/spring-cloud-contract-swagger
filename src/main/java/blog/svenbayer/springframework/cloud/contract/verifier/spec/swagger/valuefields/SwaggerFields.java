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

package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.valuefields;

/**
 * Vendor fields of a Swagger document.
 *
 * @author Sven Bayer
 */
public enum SwaggerFields {

	/**
	 * Filed for simple example declaration in swagger file.
	 */
	X_EXAMPLE("x-example"),
	/**
	 * Filed for reference example declaration in swagger file.
	 */
	X_REF("x-ref"),
	/**
	 * Mark field as ignored.
	 */
	X_IGNORE("x-ignore");

	private final String swaggerField;

	SwaggerFields(String field) {
		this.swaggerField = field;
	}

	/**
	 * Returns the string representation of the Swagger field.
	 * @return the Swagger field
	 */
	public String field() {
		return this.swaggerField;
	}

}
