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

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;

public class DefaultValuesTest {

	private DefaultValues defaultValues;

	@Before
	public void init() {
		this.defaultValues = new DefaultValues();
	}

	@DisplayName("Double with min")
	@Test
	public void doubleWithMin() {
		Object actualValue = this.defaultValues.createDefaultValueForType(
				SwaggerTypes.NUMBER.type(), SwaggerFormats.DOUBLE.format(), "myValue",
				BigDecimal.valueOf(3.4d), null);
		Assertions.assertEquals(3.4d, actualValue);
	}

	@DisplayName("Float with max")
	@Test
	public void floatWithMax() {
		Object actualValue = this.defaultValues.createDefaultValueForType(
				SwaggerTypes.NUMBER.type(), SwaggerFormats.FLOAT.format(), "myValue",
				null, BigDecimal.valueOf(3.4f));
		Assertions.assertEquals(3.4f, actualValue);
	}

	@DisplayName("Default Long")
	@Test
	public void defaultLong() {
		Object actualValue = this.defaultValues.createDefaultValueForType(
				SwaggerTypes.INTEGER.type(), SwaggerFormats.INT_64.format(), "myValue",
				null, null);
		Assertions.assertEquals(1L, actualValue);
	}

	@DisplayName("Long with max")
	@Test
	public void longWithMax() {
		Object actualValue = this.defaultValues.createDefaultValueForType(
				SwaggerTypes.INTEGER.type(), SwaggerFormats.INT_64.format(), "myValue",
				null, BigDecimal.valueOf(4));
		Assertions.assertEquals(4L, actualValue);
	}

	@DisplayName("Long with min")
	@Test
	public void longWithMin() {
		Object actualValue = this.defaultValues.createDefaultValueForType(
				SwaggerTypes.INTEGER.type(), SwaggerFormats.INT_64.format(), "myValue",
				BigDecimal.valueOf(4), null);
		Assertions.assertEquals(4L, actualValue);
	}

	@DisplayName("Default Integer")
	@Test
	public void defaultInteger() {
		Object actualValue = this.defaultValues.createDefaultValueForType(
				SwaggerTypes.INTEGER.type(), null, "myValue", null, null);
		Assertions.assertEquals(1, actualValue);
	}

	@DisplayName("Default Integer32")
	@Test
	public void defaultInteger32() {
		Object actualValue = this.defaultValues.createDefaultValueForType(
				SwaggerTypes.INTEGER.type(), SwaggerFormats.INT_32.format(), "myValue",
				null, null);
		Assertions.assertEquals(1, actualValue);
	}

	@DisplayName("Integer with max")
	@Test
	public void integerWithMax() {
		Object actualValue = this.defaultValues.createDefaultValueForType(
				SwaggerTypes.INTEGER.type(), null, "myValue", null,
				BigDecimal.valueOf(4));
		Assertions.assertEquals(4, actualValue);
	}

	@DisplayName("Integer with min")
	@Test
	public void integerWithMin() {
		Object actualValue = this.defaultValues.createDefaultValueForType(
				SwaggerTypes.INTEGER.type(), null, "myValue", BigDecimal.valueOf(4),
				null);
		Assertions.assertEquals(4, actualValue);
	}

}
