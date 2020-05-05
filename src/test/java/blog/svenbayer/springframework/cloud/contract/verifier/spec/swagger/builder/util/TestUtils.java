package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder.util;

import org.springframework.cloud.contract.spec.internal.Headers;

public class TestUtils {

	public static final String REQUEST_TYPE_HEADER_NAME = "X-Test-RequestType";

	public static final String RESPONSE_TYPE_HEADER_NAME = "X-Test-ResponseType";

	private static final String BASE_MODEL_PACKAGE = "com.kalymaha.test.model.";

	public static String getHeaderValue(final String headerName, final Headers headers) {
		return headers.getEntries().stream().filter(h -> headerName.equals(h.getName()))
				.findFirst().map(h -> BASE_MODEL_PACKAGE + h.getServerValue().toString())
				.orElseThrow(() -> new IllegalArgumentException(
						"Please include type of request and response in swagger file."));
	}

}
