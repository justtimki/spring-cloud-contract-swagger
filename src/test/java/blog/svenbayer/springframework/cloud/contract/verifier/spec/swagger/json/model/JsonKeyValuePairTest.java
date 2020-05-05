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

import java.util.HashSet;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;

public class JsonKeyValuePairTest {

	@DisplayName("Equals for same instance")
	@Test
	public void equalsForSameInstance() {
		HashSet<JsonKeyValuePair> aSet = new HashSet<>();
		aSet.add(new JsonKeyValuePair("a1", null));
		JsonKeyValuePair a = new JsonKeyValuePair("a", aSet);

		Assertions.assertEquals(a, a);
	}

	@DisplayName("Equals for same key and value")
	@Test
	public void equalsForKeyValue() {
		HashSet<JsonKeyValuePair> aSet = new HashSet<>();
		aSet.add(new JsonKeyValuePair("a1", null));
		JsonKeyValuePair a = new JsonKeyValuePair("a", aSet);

		HashSet<JsonKeyValuePair> bSet = new HashSet<>();
		bSet.add(new JsonKeyValuePair("a1", null));
		JsonKeyValuePair b = new JsonKeyValuePair("a", bSet);

		Assertions.assertEquals(a, b);
	}

	@DisplayName("Not equals for same key but different value")
	@Test
	public void notEqualsForKeyDifferentValue() {
		HashSet<JsonKeyValuePair> aSet = new HashSet<>();
		aSet.add(new JsonKeyValuePair("a1", null));
		JsonKeyValuePair a = new JsonKeyValuePair("a", aSet);

		HashSet<JsonKeyValuePair> bSet = new HashSet<>();
		bSet.add(new JsonKeyValuePair("different", null));
		JsonKeyValuePair b = new JsonKeyValuePair("a", bSet);

		Assertions.assertNotEquals(a, b);
	}

	@DisplayName("Not equals for different types")
	@Test
	public void notEqualsForDifferentTypes() {
		HashSet<JsonKeyValuePair> aSet = new HashSet<>();
		aSet.add(new JsonKeyValuePair("a1", null));
		JsonKeyValuePair a = new JsonKeyValuePair("a", aSet);

		Assertions.assertNotEquals(a, "hello");
	}

}
