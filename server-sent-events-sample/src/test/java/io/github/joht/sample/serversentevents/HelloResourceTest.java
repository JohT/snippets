package io.github.joht.sample.serversentevents;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class HelloResourceTest {

    @Test
    public void testHelloJsonEndpoint() {
        given()
          .when().get("/hello")
          .then()
          .contentType(MediaType.SERVER_SENT_EVENTS)
             .body(containsString("hello there"));
    }
    @Test
    public void testHelloTextEndpoint() {
    	given()
    	.when().get("/hello/text")
    	.then()
    	.contentType(MediaType.SERVER_SENT_EVENTS)
    	.body(containsString("hello there"));
    }
}