package io.s4.example.twittertrending;

import io.s4.core.Event;

public class TextEvent extends Event {

    private String text;

    public TextEvent(String text) {
	this.text = text;
    }

    public TextEvent() {
    }

    public String getText() {
	return text;
    }

    public void setText(String text) {
	this.text = text;
    }

    @Override
    public String toString() {
	return text;
    }

}
