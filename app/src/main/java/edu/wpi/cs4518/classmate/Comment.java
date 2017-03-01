package edu.wpi.cs4518.classmate;

import org.json.JSONException;
import org.json.JSONObject;

public class Comment {
    private String commentText;
    private String timestamp;
    private String author;

    Comment() {}

    Comment(JSONObject jObject){
        try {
            this.commentText = jObject.getString("commentText");
            this.timestamp = jObject.getString("timestamp");
            this.author = jObject.getString("author");
        }        catch (JSONException e) {}

    }

    String getCommentText(){
        return this.commentText;
    }

    String getTimestamp(){
        return this.timestamp;
    }

    String getAuthor(){
        return this.author;
    }
}
