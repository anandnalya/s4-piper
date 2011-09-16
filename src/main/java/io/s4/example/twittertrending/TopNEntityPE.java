package io.s4.example.twittertrending;

import io.s4.core.App;
import io.s4.core.Event;
import io.s4.core.ProcessingElement;
import io.s4.core.Stream;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONObject;

public class TopNEntityPE extends ProcessingElement {

    private int entryCount = 10;
    private Map<String, Integer> entityMap;
    private Stream<TextEvent> textStream;

    public TopNEntityPE(App app, Stream<TextEvent> textStream) {
	super(app);
	this.textStream = textStream;
    }

    @Override
    protected void processInputEvent(Event event) {
	EntityEvent entityEvent = (EntityEvent) event;
	entityMap.put(entityEvent.getEntity(), entityEvent.getCount());

    }

    public ArrayList<TopNEntry> getTopEntities() {
	if (entryCount < 1)
	    return null;

	ArrayList<TopNEntry> sortedList = new ArrayList<TopNEntry>();

	for (String key : entityMap.keySet()) {
	    sortedList.add(new TopNEntry(key, entityMap.get(key)));
	}

	Collections.sort(sortedList);

	// truncate: Yuck!!
	// unfortunately, Kryo cannot deserialize RandomAccessSubList
	// if we use ArrayList.subList(...)
	while (sortedList.size() > entryCount)
	    sortedList.remove(sortedList.size() - 1);

	return sortedList;
    }

    @Override
    public void processOutputEvent(Event event) {
	List<TopNEntry> sortedList = new ArrayList<TopNEntry>();

	for (String key : entityMap.keySet()) {
	    sortedList.add(new TopNEntry(key, entityMap.get(key)));
	}

	Collections.sort(sortedList);

	try {
	    JSONObject message = new JSONObject();
	    StringBuilder builder = new StringBuilder(
		    ((EntityEvent) event).getReportKey()).append(": ");
	    for (int i = 0; i < entryCount; i++) {
		if (i == sortedList.size()) {
		    break;
		}
		TopNEntry tne = sortedList.get(i);

		builder.append(tne.getEntity()).append(':')
			.append(tne.getCount()).append(", ");
	    }
	    textStream.put(new TextEvent(builder.toString()));
	    write(message.toString() + "\n");
	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

    private void write(String string) {
	try {
	    FileWriter writer = new FileWriter("twitter1.log");
	    writer.append(string);
	    writer.flush();
	    writer.close();
	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

    @Override
    protected void onCreate() {
	entityMap = new ConcurrentHashMap<String, Integer>();

    }

    @Override
    protected void onRemove() {
	// TODO Auto-generated method stub

    }

    public static class TopNEntry implements Comparable<TopNEntry> {
	public TopNEntry(String entity, int count) {
	    this.entity = entity;
	    this.count = count;
	}

	public TopNEntry() {
	}

	String entity = null;
	int count = 0;

	public String getEntity() {
	    return entity;
	}

	public void setEntity(String entity) {
	    this.entity = entity;
	}

	public int getCount() {
	    return count;
	}

	public void setCount(int count) {
	    this.count = count;
	}

	public int compareTo(TopNEntry topNEntry) {
	    if (topNEntry.getCount() < this.count) {
		return -1;
	    } else if (topNEntry.getCount() > this.count) {
		return 1;
	    }
	    return 0;
	}

	public String toString() {
	    return "entity:" + entity + " count:" + count;
	}
    }

}
