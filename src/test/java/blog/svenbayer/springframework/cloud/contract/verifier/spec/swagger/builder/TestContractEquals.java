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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder.util.TestUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;
import org.springframework.cloud.contract.spec.Contract;
import org.springframework.cloud.contract.spec.internal.Body;
import org.springframework.cloud.contract.spec.internal.DslProperty;
import org.springframework.cloud.contract.spec.internal.Header;
import org.springframework.cloud.contract.spec.internal.Headers;
import org.springframework.cloud.contract.spec.internal.QueryParameter;
import org.springframework.cloud.contract.spec.internal.Request;
import org.springframework.cloud.contract.spec.internal.Response;
import org.springframework.cloud.contract.spec.internal.UrlPath;

/**
 * Class Helper for assertions in tests.
 *
 * @author Sven Bayer
 */
public final class TestContractEquals {

	private static final String LINE_SEPS = "\\r\\n|\\n|\\r";

	private static final ObjectMapper objectMapper = new ObjectMapper();

	private TestContractEquals() {
	}

	public static void assertContractEquals(Collection<Contract> expected,
			Collection<Contract> actual) {
		final String msg = "Contract List:\n";
		if (assertBothOrNonNull(expected, actual, msg)) {
			return;
		}
		Assertions.assertEquals(expected.size(), actual.size());

		Iterator<Contract> expectedIterator = expected.iterator();
		Iterator<Contract> actualIterator = actual.iterator();

		List<Executable> contractExecutables = new ArrayList<>();
		AtomicInteger index = new AtomicInteger(0);
		while (expectedIterator.hasNext()) {
			final Contract expectedNext = expectedIterator.next();
			final Contract actualNext = actualIterator.next();
			final String contractMsg = msg + "Contract List Index: "
					+ index.getAndIncrement() + "\n";
			contractExecutables.add(
					() -> assertContractEquals(expectedNext, actualNext, contractMsg));
		}

		Assertions.assertAll("contract list", contractExecutables.stream());
	}

	private static void assertContractEquals(Contract expected, Contract actual,
			String msg) {
		if (assertBothOrNonNull(expected, actual, msg)) {
			return;
		}

		Assertions.assertEquals(expected.getName(), actual.getName());
		msg = msg + "Contract: " + expected.getName() + "\n";

		Assertions.assertEquals(expected.getPriority(), actual.getPriority(), msg);
		Assertions.assertEquals(expected.getLabel(), actual.getLabel(), msg);
		Assertions.assertEquals(expected.getDescription(), actual.getDescription(), msg);
		Assertions.assertEquals(expected.isIgnored(), actual.isIgnored(), msg);
		Assertions.assertEquals(expected.getInput(), actual.getInput(), msg);
		Assertions.assertEquals(expected.getOutputMessage(), actual.getOutputMessage(),
				msg);

		assertRequestEquals(expected, actual, msg);

		assertResponseEquals(expected, actual, msg);
	}

	private static void assertResponseEquals(Contract expected, Contract actual,
			String msg) {
		msg = msg + "Response:\n";
		Response actualResponse = actual.getResponse();
		Response expectedResponse = expected.getResponse();
		if (assertBothOrNonNull(expectedResponse, actualResponse, msg)) {
			return;
		}

		assertDslPropertyEquals(expectedResponse.getStatus(), actualResponse.getStatus(),
				msg);
		assertDslPropertyEquals(expectedResponse.getDelay(), actualResponse.getDelay(),
				msg);
		assertHeadersEquals(expectedResponse.getHeaders(), actualResponse.getHeaders(),
				msg);
		Assertions.assertEquals(expectedResponse.getCookies(),
				actualResponse.getCookies(), msg);
		assertJsonEquals(
				TestUtils.getHeaderValue(TestUtils.RESPONSE_TYPE_HEADER_NAME,
						expectedResponse.getHeaders()),
				expectedResponse.getBody(), actualResponse.getBody(), msg);
		Assertions.assertEquals(expectedResponse.isAsync(), actualResponse.isAsync(),
				msg);
		Assertions.assertEquals(expectedResponse.getBodyMatchers(),
				actualResponse.getBodyMatchers(), msg);
	}

	private static void assertRequestEquals(Contract expected, Contract actual,
			String msg) {
		msg = msg + "Request:\n";
		Request actualRequest = actual.getRequest();
		Request expectedRequest = expected.getRequest();
		if (assertBothOrNonNull(expectedRequest, actualRequest, msg)) {
			return;
		}

		assertDslPropertyEquals(expectedRequest.getMethod(), actualRequest.getMethod(),
				msg);
		Assertions.assertEquals(expectedRequest.getUrl(), actualRequest.getUrl(), msg);
		assertUrlPathEquals(expectedRequest.getUrlPath(), actualRequest.getUrlPath(),
				msg);
		assertHeadersEquals(expectedRequest.getHeaders(), actualRequest.getHeaders(),
				msg);
		Assertions.assertEquals(expectedRequest.getCookies(), actualRequest.getCookies(),
				msg);
		assertJsonEquals(
				TestUtils.getHeaderValue(TestUtils.REQUEST_TYPE_HEADER_NAME,
						expectedRequest.getHeaders()),
				expectedRequest.getBody(), actualRequest.getBody(), msg);
		Assertions.assertEquals(expectedRequest.getMultipart(),
				actualRequest.getMultipart(), msg);
		Assertions.assertEquals(expectedRequest.getBodyMatchers(),
				actualRequest.getBodyMatchers(), msg);
	}

	private static void assertUrlPathEquals(UrlPath expected, UrlPath actual,
			String msg) {
		if (assertBothOrNonNull(expected, actual, msg)) {
			return;
		}

		Assertions.assertAll("urlpath",
				() -> assertPossiblePatternEquals(expected.getClientValue(),
						actual.getClientValue(), msg + "ClientValue:\n"),
				() -> assertPossiblePatternEquals(expected.getServerValue(),
						actual.getServerValue(), msg + "ServerValue:\n"));

		assertQueryParametersEquals(expected, actual, msg);
	}

	private static void assertQueryParametersEquals(UrlPath expected, UrlPath actual,
			String msg) {
		if (assertBothOrNonNull(expected.getQueryParameters(),
				actual.getQueryParameters(), msg)) {
			return;
		}
		List<QueryParameter> expectedParameters = expected.getQueryParameters()
				.getParameters();
		List<QueryParameter> actualParameters = actual.getQueryParameters()
				.getParameters();
		if (assertBothOrNonNull(expectedParameters, actualParameters, msg)) {
			return;
		}

		Assertions.assertEquals(expectedParameters.size(), actualParameters.size(), msg);

		List<Executable> queryParameterExecutables = new ArrayList<>();
		final String baseMsg = msg;
		for (int i = 0; i < expectedParameters.size(); i++) {
			QueryParameter expectedParameter = expectedParameters.get(i);
			QueryParameter actualParameter = actualParameters.get(i);

			queryParameterExecutables.add(() -> {
				Assertions.assertEquals(expectedParameter.getName(),
						actualParameter.getName(), msg);
				final String queryMsg = baseMsg + "QueryParameter: "
						+ expectedParameter.getName() + "\n";

				Assertions.assertAll("query parameter",
						() -> assertPossiblePatternEquals(
								expectedParameter.getClientValue(),
								actualParameter.getClientValue(),
								queryMsg + "ClientValue:\n"),
						() -> assertPossiblePatternEquals(
								expectedParameter.getServerValue(),
								actualParameter.getServerValue(),
								queryMsg + "ServerValue:\n"));
				Assertions.assertEquals(expectedParameter.isSingleValue(),
						actualParameter.isSingleValue(), msg);
			});
		}
		Assertions.assertAll("query parameters", queryParameterExecutables.stream());
	}

	static void assertDslPropertyEquals(DslProperty expected, DslProperty actual,
			String msg) {
		if (assertBothOrNonNull(expected, actual, msg)) {
			return;
		}

		Assertions.assertAll("dsl property",
				() -> assertPossiblePatternEquals(expected.getClientValue(),
						actual.getClientValue(), msg + "ClientValue:\n"),
				() -> assertPossiblePatternEquals(expected.getServerValue(),
						actual.getServerValue(), msg + "ServerValue:\n"));
		Assertions.assertEquals(expected.isSingleValue(), actual.isSingleValue(), msg);
	}

	private static void assertHeadersEquals(Headers expected, Headers actual,
			String msg) {
		if (assertBothOrNonNull(expected, actual, msg)) {
			return;
		}
		assertHeaderEquals(expected.getEntries(), actual.getEntries(), msg);

		Assertions.assertEquals(expected.contentType(), actual.contentType(), msg);
		Assertions.assertEquals(expected.messagingContentType(),
				actual.messagingContentType(), msg);
	}

	private static void assertHeaderEquals(Set<Header> expected, Set<Header> actual,
			String msg) {
		if (assertBothOrNonNull(expected, actual, msg)) {
			return;
		}
		Iterator<Header> expectedIterator = expected.iterator();
		Iterator<Header> actualIterator = actual.iterator();

		List<Executable> headerExecutables = new ArrayList<>();
		while (expectedIterator.hasNext()) {
			Header expectedHeader = expectedIterator.next();
			Header actualHeader = actualIterator.next();

			headerExecutables.add(() -> {
				Assertions.assertEquals(expectedHeader.getName(), actualHeader.getName(),
						msg);
				final String headerMsg = msg + "Header: " + expectedHeader.getName()
						+ "\n";
				Assertions.assertAll("header",
						() -> assertPossiblePatternEquals(expectedHeader.getClientValue(),
								actualHeader.getClientValue(),
								headerMsg + "ClientValue:\n"),
						() -> assertPossiblePatternEquals(expectedHeader.getServerValue(),
								actualHeader.getServerValue(),
								headerMsg + "ServerValue:\n"));
			});
		}
		Assertions.assertAll("header", headerExecutables.stream());
	}

	private static void assertJsonEquals(final String headerValue, final Object expected,
			final Object actual, final String msg) {
		if (StringUtils.isEmpty(headerValue)) {
			assertEqualsNoLineSeparator(expected, actual, msg);
		}
		else if (expected instanceof Body) {
			try {
				final Object expectedObject = objectMapper.readValue(
						((Body) expected).getClientValue().toString(),
						Class.forName(headerValue));
				final Object actualObject = objectMapper.readValue(
						((Body) actual).getClientValue().toString(),
						Class.forName(headerValue));

				Assertions.assertEquals(expectedObject, actualObject, msg);
			}
			catch (JsonProcessingException | ClassNotFoundException e) {
				Assertions.fail("Can't compare definitions from swagger file.", e);
			}
		}
	}

	private static void assertPossiblePatternEquals(Object expected, Object actual,
			String msg) {
		if (expected instanceof Pattern) {
			msg = msg + "Pattern:\n";
			Assertions.assertEquals(Pattern.class, actual.getClass(), msg);
			Pattern expectedPattern = (Pattern) expected;
			Pattern actualPattern = (Pattern) actual;
			Assertions.assertEquals(expectedPattern.pattern(), actualPattern.pattern(),
					msg);
		}
		else if (expected instanceof DslProperty) {
			msg = msg + "DslProperty:\n";
			DslProperty expectedDslProperty = (DslProperty) expected;
			DslProperty actualDslProperty = (DslProperty) actual;
			assertDslPropertyEquals(expectedDslProperty, actualDslProperty, msg);
		}
		else {
			assertEqualsNoLineSeparator(expected, actual, msg);
		}
	}

	private static boolean assertBothOrNonNull(Object expected, Object actual,
			String msg) {
		if (expected == null) {
			Assertions.assertNull(actual, msg);
			return true;
		}
		else {
			Assertions.assertNotNull(actual, msg);
			return false;
		}
	}

	private static void assertEqualsNoLineSeparator(Object expected, Object actual,
			String msg) {
		if (expected == null) {
			Assertions.assertNull(actual, msg);
		}
		else {
			Assertions.assertNotNull(actual, msg);
			String cleanedUpExpected = expected.toString().replaceAll(LINE_SEPS,
					System.lineSeparator());
			String cleanedUpActual = actual.toString().replaceAll(LINE_SEPS,
					System.lineSeparator());
			Assertions.assertEquals(cleanedUpExpected, cleanedUpActual, msg);
		}
	}

}
