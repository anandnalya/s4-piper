package io.s4.example.twittertrending;

import java.util.Collections;
import java.util.List;

import io.s4.core.KeyFinder;

public class TextKeyFinder implements KeyFinder<TextEvent> {

    @Override
    public List<String> get(TextEvent event) {
	return Collections.singletonList(event.getClass().getName());
    }

}
