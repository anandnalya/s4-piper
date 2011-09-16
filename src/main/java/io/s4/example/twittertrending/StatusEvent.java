package io.s4.example.twittertrending;

import io.s4.core.Event;

public class StatusEvent extends Event {
    private long id;
    private String text;
    private String source;
    private User user;
    private String createdAt;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("{")
          .append("id:")
          .append(id)
          .append(",")
          .append(",")
          .append("text:")
          .append(text)
          .append(",")
          .append(",")
          .append("source:")
          .append(source)
          .append(",")
          .append(",")
          .append(",")
          .append("user:")
          .append(user)
          .append(",")
          .append(",")
          .append("createdAt:")
          .append(createdAt)
          .append("}");

        return sb.toString();
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException cnse) {
            throw new RuntimeException(cnse);
        }
    }
}