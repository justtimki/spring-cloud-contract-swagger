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

/**
 * Default values that we set in a Spring Cloud Contract if no example value is set.
 *
 * @author Sven Bayer
 */
public final class DefaultValues {

	/**
	 * The default value for floats.
	 */
	public static final double DEFAULT_DOUBLE = 1.1d;

	/**
	 * The default value for double.
	 */
	public static final double DEFAULT_FLOAT = 1.1f;

	/**
	 * The default value for longs, int64.
	 */
	public static final long DEFAULT_LONG = 1L;

	/**
	 * The default value for integers and anything undefined.
	 */
	public static final int DEFAULT_INT = 1;

	/**
	 * The default value for booleans.
	 */
	public static final boolean DEFAULT_BOOLEAN = true;

	/**
	 * Creates a default value for a given parameter.
	 * @param type the primitive type
	 * @param format the specific format
	 * @param name the name of the parameter
	 * @param min minimum value
	 * @param max maximum value
	 * @return the default value
	 */
	public Object createDefaultValueForType(String type, String format, String name,
			BigDecimal min, BigDecimal max) {
		if (SwaggerTypes.STRING.type().equals(type)) {
			if (name != null && !name.isEmpty()) {
				return name;
			}
			else {
				return SwaggerTypes.STRING.type();
			}
		}
		Object numericalValue = getNumericalValue(type, format, min, max);
		if (numericalValue != null) {
			return numericalValue;
		}
		Object intValue = getIntValue(type, format, min, max);
		if (intValue != null) {
			return intValue;
		}
		Object longValue = getLongValue(type, format, min, max);
		if (longValue != null) {
			return longValue;
		}
		if (SwaggerTypes.BOOLEAN.type().equals(type)) {
			return DEFAULT_BOOLEAN;
		}
		return DEFAULT_INT;
	}

	private Object getIntValue(String type, String format, BigDecimal min,
			BigDecimal max) {
		if (SwaggerTypes.INTEGER.type().equals(type)
				&& SwaggerFormats.INT_64.format().equals(format)) {
			if (min != null) {
				return min.longValue();
			}
			if (max != null) {
				return max.longValue();
			}
			return DEFAULT_LONG;
		}
		return null;
	}

	private Object getLongValue(String type, String format, BigDecimal min,
			BigDecimal max) {
		if (SwaggerTypes.INTEGER.type().equals(type)
				&& (SwaggerFormats.INT_32.format().equals(format) || format == null)) {
			if (min != null) {
				return min.intValue();
			}
			if (max != null) {
				return max.intValue();
			}
			return DEFAULT_INT;
		}
		return null;
	}

	private Object getNumericalValue(String type, String format, BigDecimal min,
			BigDecimal max) {
		if (SwaggerTypes.NUMBER.type().equals(type)
				&& (SwaggerFormats.DOUBLE.format().equals(format) || format == null)) {
			if (min != null) {
				return min.doubleValue();
			}
			if (max != null) {
				return max.doubleValue();
			}
			return DEFAULT_DOUBLE;
		}
		if (SwaggerTypes.NUMBER.type().equals(type)
				&& SwaggerFormats.FLOAT.format().equals(format)) {
			if (min != null) {
				return min.floatValue();
			}
			if (max != null) {
				return max.floatValue();
			}
			return DEFAULT_DOUBLE;
		}
		return null;
	}

}
