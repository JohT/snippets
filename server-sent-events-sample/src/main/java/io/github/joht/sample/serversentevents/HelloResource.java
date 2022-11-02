package io.github.joht.sample.serversentevents;

import jakarta.json.Json;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;

@Path("/hello")
public class HelloResource {

	@Context
	Sse serverSentEvents;

	@GET
	@Produces(MediaType.SERVER_SENT_EVENTS + ";element-type=" + MediaType.APPLICATION_JSON)
	public void helloFailsWithoutReflectionConfig(//
			@HeaderParam(HttpHeaders.LAST_EVENT_ID_HEADER) @DefaultValue("-1") long lastEventId,
			@Context SseEventSink eventSink) {
		
		Greeting data = new Greeting("hello there");
		eventSink.send(serverSentEvents.newEventBuilder() //
				.mediaType(MediaType.APPLICATION_JSON_TYPE)
				.data(data)
				.id(Long.toString(lastEventId + 1))
				.reconnectDelay(5000)
				.name(data.getClass().getSimpleName())
				.build());
	}

	@GET
	@Path("/text")
	@Produces(MediaType.SERVER_SENT_EVENTS)
	public void helloAsText(//
			@HeaderParam(HttpHeaders.LAST_EVENT_ID_HEADER) @DefaultValue("-1") long lastEventId,
			@Context SseEventSink eventSink) {
		eventSink.send(serverSentEvents.newEventBuilder() //
				.mediaType(MediaType.TEXT_PLAIN_TYPE)
				.data("hello there").id(Long.toString(lastEventId + 1))
				.reconnectDelay(5000)
				.name("TextGreeting")
				.build());
	}
	
	@GET
	@Path("/jsonp")
	@Produces(MediaType.SERVER_SENT_EVENTS + ";element-type=" + MediaType.APPLICATION_JSON)
	public void helloJsonP(//
			@HeaderParam(HttpHeaders.LAST_EVENT_ID_HEADER) @DefaultValue("-1") long lastEventId,
			@Context SseEventSink eventSink) {
		
		String json = Json.createObjectBuilder().add("text", "hello there").build().toString();
		eventSink.send(serverSentEvents.newEventBuilder() //
				.mediaType(MediaType.APPLICATION_JSON_TYPE)
				.data(json)
				.id(Long.toString(lastEventId + 1))
				.reconnectDelay(5000)
				.name("JsonpGreeting")
				.build());
	}
}