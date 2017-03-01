package edu.wpi.cs4518.classmate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ClassMateEvent {

    private String id;
    private String title;
    private String semanticLocation;
    private double latitude;
    private double longitude;
    private int usersAttending;
    private Date startTime;
    private Date endTime;

    ClassMateEvent(
            String id,
            String title,
            String semanticLocation,
            double latitude,
            double longitude,
            int usersAttending,
            Date startTime,
            Date endTime
    ) {
        this.id = id;
        this.title = title;
        this.semanticLocation = semanticLocation;
        this.latitude = latitude;
        this.longitude = longitude;
        this.usersAttending = usersAttending;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    ClassMateEvent(JSONObject eventJSON) throws JSONException {
        this.id = eventJSON.getString("id");
        this.title = eventJSON.getString("title");
        this.semanticLocation = eventJSON.getString("semanticLocation");
        this.latitude = eventJSON.getDouble("latitude");
        this.longitude = eventJSON.getDouble("longitude");
        this.usersAttending = eventJSON.getInt("usersAttending");
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            this.startTime = dateFormat.parse(eventJSON.getString("startTime"));
            this.endTime = dateFormat.parse(eventJSON.getString("endTime"));
        }
        catch(ParseException e) {
            e.printStackTrace();
        }
    }

    String getId() {
        return id;
    }

    String getTitle() {
        return title;
    }

    String getSemanticLocation() {
        return semanticLocation;
    }

    double getLatitude() {
        return latitude;
    }

    double getLongitude() {
        return longitude;
    }

    int getUsersAttending() {
        return usersAttending;
    }

    Date getStartTime() {
        return startTime;
    }

    Date getEndTime() {
        return endTime;
    }

    String getDateString() {
        return new SimpleDateFormat("MMMM dd, yyyy", Locale.US).format(startTime);
    }

    String getTimeString() {
        String startTimeString = new SimpleDateFormat("hh:mm a", Locale.US).format(startTime);
        String endTimeString = new SimpleDateFormat("hh:mm a", Locale.US).format(endTime);
        return startTimeString + " - " + endTimeString;
    }

    static List<ClassMateEvent> parseJSONArray(JSONArray response) {
        List<ClassMateEvent> eventList = new ArrayList<ClassMateEvent>();
        for(int i = 0; i < response.length(); i++) {
            try{
                eventList.add(new ClassMateEvent(response.getJSONObject(i)));
            }
            catch(JSONException e) {
                e.printStackTrace();
            }
        }
        return eventList;
    }
}
