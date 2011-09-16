package io.s4.example.twittertrending;

public class User {
    private long id;
    private String screenName;
    private String name;
    private String url;
    private String location;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("{")
          .append("id:")
          .append(id)
          .append(",")
          .append("screenName:")
          .append(screenName)
          .append(",")
          .append("name:")
          .append(name)
          .append(",")
          .append("url:")
          .append(url)
          .append(",")
          .append("location:")
          .append(location)
          .append("}");

        return sb.toString();

    }

}