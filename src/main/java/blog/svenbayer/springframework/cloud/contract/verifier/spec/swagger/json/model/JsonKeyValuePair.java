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

package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.json.model;

import java.util.Objects;
import java.util.Set;

/**
 * A Json key-value representation.
 *
 * @author Sven Bayer
 */
public class JsonKeyValuePair {

	private final String key;

	private final Set<JsonKeyValuePair> value;

	public JsonKeyValuePair(String key, Set<JsonKeyValuePair> value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof JsonKeyValuePair)) {
			return false;
		}
		JsonKeyValuePair that = (JsonKeyValuePair) o;
		return Objects.equals(this.key, that.key)
				&& Objects.equals(this.value, that.value);
	}

	@Override
	public int hashCode() {
		int valueHashCode = 0;
		if (this.value != null) {
			valueHashCode = this.value.stream().mapToInt(Object::hashCode).sum();
		}
		return Objects.hashCode(this.key) + valueHashCode;
	}

}
