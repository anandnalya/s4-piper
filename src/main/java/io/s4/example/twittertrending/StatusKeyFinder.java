package io.s4.example.twittertrending;

import java.util.Collections;
import java.util.List;

import io.s4.core.KeyFinder;

public class StatusKeyFinder implements KeyFinder<StatusEvent> {

    @Override
    public List<String> get(StatusEvent event) {
	return Collections.singletonList(event.getClass().getName());
    }

}
