package edu.wpi.cs4518.classmate;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ClassMateEvent {

    private String id;
    private String title;
    private String semanticLocation;
    private double latitude;
    private double longitude;
    private int usersAttending;
    private Date startTime;
    private Date endTime;

    public ClassMateEvent(
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

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSemanticLocation() {
        return semanticLocation;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getUsersAttending() {
        return usersAttending;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public String getDateString() {
        return new SimpleDateFormat("MMMM dd, yyyy").format(startTime);
    }

    public String getTimeString() {
        String startTimeString = new SimpleDateFormat("hh:mm a").format(startTime);
        String endTimeString = new SimpleDateFormat("hh:mm a").format(endTime);
        return startTimeString + " - " + endTimeString;
    }
}
