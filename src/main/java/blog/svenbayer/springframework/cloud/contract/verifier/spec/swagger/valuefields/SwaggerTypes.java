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
 * Types of a Swagger document.
 *
 * @author Sven Bayer
 */
public enum SwaggerTypes {

	/**
	 * String swagger type.
	 */
	STRING("string"),
	/**
	 * Number swagger type.
	 */
	NUMBER("number"),
	/**
	 * Boolean swagger type.
	 */
	BOOLEAN("boolean"),
	/**
	 * Integer swagger type.
	 */
	INTEGER("integer");

	private final String type;

	SwaggerTypes(String swaggerType) {
		this.type = swaggerType;
	}

	/**
	 * Returns the Swagger type as string.
	 * @return the Swagger type
	 */
	public String type() {
		return this.type;
	}

}
