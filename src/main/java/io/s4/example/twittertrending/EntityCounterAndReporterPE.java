package io.s4.example.twittertrending;

import io.s4.core.App;
import io.s4.core.Event;
import io.s4.core.ProcessingElement;
import io.s4.core.Stream;

public class EntityCounterAndReporterPE extends ProcessingElement {

    private Stream<EntityEvent> aggreagatedEntityStream;
    private int threshold;
    private int count;
    private String reporteKey;

    public EntityCounterAndReporterPE(App app,
	    Stream<EntityEvent> aggreagatedEntityStream, int threshold,
	    String reportKey) {
	super(app);
	this.aggreagatedEntityStream = aggreagatedEntityStream;
	this.threshold = threshold;
	this.count = 0;
	this.reporteKey = reportKey;
    }

    @Override
    protected void processInputEvent(Event event) {
	count += ((EntityEvent) event).getCount();

    }

    @Override
    public void processOutputEvent(Event event) {
	if (count < threshold) {
	    return;
	}

	EntityEvent entityEvent = new EntityEvent(((EntityEvent) event).getEntity(),
		count);
	entityEvent.setReportKey(reporteKey);
	aggreagatedEntityStream.put(entityEvent);

    }

    @Override
    protected void onCreate() {
	// TODO Auto-generated method stub

    }

    @Override
    protected void onRemove() {
	// TODO Auto-generated method stub

    }

}
