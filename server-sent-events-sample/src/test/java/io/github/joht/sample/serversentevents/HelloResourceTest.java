package io.github.joht.sample.serversentevents;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.InboundSseEvent;
import jakarta.ws.rs.sse.SseEventSource;

@QuarkusTest
class HelloResourceTest {

	private List<String> receivedDataFields = new ArrayList<>();

	@TestHTTPResource("/hello")
	URL url;

	@Test
	public void helloJsonEndpointReturnsExpectedResponseInStream() throws IOException, InterruptedException {
		final String subResource = "";
		expectResponseToContain("hello there", subResource);
	}

	@Test
	public void helloTextEndpointReturnsExpectedResponseInStream() throws IOException, InterruptedException {
		final String subResource = "/text";
		expectResponseToContain("hello there", subResource);
	}

	@Test
	public void helloJsonpEndpointReturnsExpectedResponseInStream() throws IOException, InterruptedException {
		final String subResource = "/jsonp";
		expectResponseToContain("hello there", subResource);
	}

	private void expectResponseToContain(String expectedResponseText, String subResource) throws InterruptedException {
		try (SseEventSource sse = createServerSentEventsSource(subResource)) {
			sse.register(this::onServerSentEventMessage);
			sse.open();

			for (int i = 0; i < 6; i++) {
				Thread.sleep(100);
				if (receivedDataFields.toString().contains(expectedResponseText)) {
					return;
				}
			}
			fail("Missing expected response " + expectedResponseText + " in stream " + receivedDataFields);
		}
	}

	private SseEventSource createServerSentEventsSource(String subResource) {
		WebTarget client = ClientBuilder.newClient().target(url.toExternalForm() + subResource);
		return SseEventSource.target(client).reconnectingEvery(5, TimeUnit.SECONDS).build();
	}

	void onServerSentEventMessage(InboundSseEvent event) {
		receivedDataFields.add(event.readData());
	}

}