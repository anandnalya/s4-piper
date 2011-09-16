package io.s4.example.twittertrending;

import java.util.Collections;
import java.util.List;

import io.s4.core.KeyFinder;

public class EntityKeyFinder implements KeyFinder<EntityEvent> {

    @Override
    public List<String> get(EntityEvent event) {
	return Collections.singletonList(event.getEntity());
    }

}
