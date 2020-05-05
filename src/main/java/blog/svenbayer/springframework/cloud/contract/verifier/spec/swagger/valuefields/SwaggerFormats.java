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
 * Represents Swagger formats, which are more detailed than types.
 *
 * @author Sven Bayer
 */
public enum SwaggerFormats {

	/**
	 * Double swagger format.
	 */
	DOUBLE("double"),
	/**
	 * Float swagger format.
	 */
	FLOAT("float"),
	/**
	 * Int 32 swagger format.
	 */
	INT_32("int32"),
	/**
	 * Int 64 swagger format.
	 */
	INT_64("int64"),
	/**
	 * Byte swagger format.
	 */
	BYTE("byte"),
	/**
	 * Binary swagger format.
	 */
	BINARY("binary"),
	/**
	 * Date swagger format.
	 */
	DATE("date"),
	/**
	 * Date and time swagger format.
	 */
	DATE_TIME("date-time"),
	/**
	 * Password swagger format.
	 */
	PASSWORD("password");

	private final String format;

	SwaggerFormats(String swaggerFormat) {
		this.format = swaggerFormat;
	}

	public String format() {
		return this.format;
	}

}
