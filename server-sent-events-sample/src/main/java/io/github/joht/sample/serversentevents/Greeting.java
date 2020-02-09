package io.github.joht.sample.serversentevents;

import java.beans.ConstructorProperties;

public class Greeting {

	private String text;

	public Greeting() {
		super();
	}

	@ConstructorProperties({"text",})
	public Greeting(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	@Override
	public String toString() {
		return "Greeting [text=" + text + "]";
	}
}
