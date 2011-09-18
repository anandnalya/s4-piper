package io.s4.example.twittertrending;

import io.s4.core.Stream;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.util.EncodingUtil;
import org.json.JSONException;
import org.json.JSONObject;

public class TwitterFeedListener {
    private String userid;
    private String password;
    private String urlString = "http://stream.twitter.com/1/statuses/sample.json";
    private long maxBackoffTime = 30 * 1000; // 5 seconds
    private long messageCount = 0;
    private long blankCount = 0;
    private Stream<StatusEvent> statusStream;
    private LinkedBlockingQueue<String> messageQueue = new LinkedBlockingQueue<String>();

    public TwitterFeedListener(Stream<StatusEvent> statusStream) {
        this.statusStream = statusStream;
    }

    public void init() {
        (new Thread(new Dequeuer())).start();
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUrlString(String urlString) {
        this.urlString = urlString;
    }

    public void setMaxBackoffTime(long maxBackoffTime) {
        this.maxBackoffTime = maxBackoffTime;
    }

    public void run() {
        long backoffTime = 1000;
        while (!Thread.interrupted()) {
            try {
                connectAndRead();
            } catch (Exception e) {
                try {
                    Thread.sleep(backoffTime);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
                backoffTime = backoffTime * 2;
                if (backoffTime > maxBackoffTime) {
                    backoffTime = maxBackoffTime;
                }
            }
        }
    }

    public void connectAndRead() throws Exception {
        URL url = new URL(urlString);

        URLConnection connection = url.openConnection();
        String userPassword = userid + ":" + password;
        String encoded = EncodingUtil.getAsciiString(Base64
                .encodeBase64(EncodingUtil.getAsciiBytes(userPassword)));
        connection.setRequestProperty("Authorization", "Basic " + encoded);
        connection.connect();
        System.out.println("connected");

        InputStream is = connection.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        String inputLine = null;
        while ((inputLine = br.readLine()) != null) {
            System.out.println(inputLine);
            if (inputLine.trim().length() == 0) {
                blankCount++;
                continue;
            }
            messageCount++;

            messageQueue.add(inputLine);
        }
    }

    class Dequeuer implements Runnable {
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    String message = messageQueue.take();
                    JSONObject messageJSON = new JSONObject(message);

                    // ignore delete records for now
                    if (messageJSON.has("delete")) {
                        continue;
                    }

                    // create a copy with some renamed fields
                    StatusEvent event = parseStatus(messageJSON);
                    statusStream.put(event);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                } catch (JSONException je) {
                    je.printStackTrace();
                }
            }
        }

        public StatusEvent parseStatus(JSONObject jsonObj) {
            if (jsonObj == null || jsonObj.equals(JSONObject.NULL)) {
                return null;
            }

            try {
                StatusEvent event = new StatusEvent();
                event.setCreatedAt(jsonObj.getString("created_at"));
                event.setId(jsonObj.getLong("id"));
                event.setSource(jsonObj.getString("source"));
                event.setText(jsonObj.getString("text"));

                JSONObject userJson = jsonObj.getJSONObject("user");
                User user = new User();
                user.setId(userJson.getLong("id"));
                user.setLocation(userJson.getString("location"));
                user.setName(userJson.getString("screen_name"));

                event.setUser(user);

                return event;
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }
    }
}
