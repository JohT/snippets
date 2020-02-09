package io.github.joht.sample.serversentevents;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;

@Path("/hello")
public class HelloResource {

	@Context
	Sse serverSentEvents;

	@GET
	@Produces(MediaType.SERVER_SENT_EVENTS + ";element-type=" + MediaType.APPLICATION_JSON)
	public void hello(//
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
		eventSink.close();
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
		eventSink.close();
	}

}