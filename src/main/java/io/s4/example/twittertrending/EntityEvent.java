package io.s4.example.twittertrending;

import io.s4.core.Event;

public class EntityEvent extends Event {
    private String entity;
    private int count;
    private String reportKey;

    public EntityEvent() {
    }

    public EntityEvent(String entity, int count) {
	this.entity = entity;
	this.count = count;
    }

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

    public String getReportKey() {
	return reportKey;
    }

    public void setReportKey(String reportKey) {
	this.reportKey = reportKey;
    }

    public String toString() {
	return "{" + entity + ":" + count + "}";
    }

    public Object clone() {
	try {
	    return super.clone();
	} catch (CloneNotSupportedException cnse) {
	    throw new RuntimeException(cnse);
	}
    }
}
