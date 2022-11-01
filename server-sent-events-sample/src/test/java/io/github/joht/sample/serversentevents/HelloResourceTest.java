package io.github.joht.sample.serversentevents;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.MediaType;

@QuarkusTest
class HelloResourceTest {

	@Test
	void testHelloJsonEndpoint() {
		given().when().get("/hello").then().contentType(MediaType.SERVER_SENT_EVENTS)
				.body(containsString("hello there"));
	}

	@Test
	void testHelloTextEndpoint() {
		given().when().get("/hello/text").then().contentType(MediaType.SERVER_SENT_EVENTS)
				.body(containsString("hello there"));
	}

	@Test
	void testHelloJsonpEndpoint() {
		given().when().get("/hello/jsonp").then().contentType(MediaType.SERVER_SENT_EVENTS)
				.body(containsString("hello there"));
	}
}