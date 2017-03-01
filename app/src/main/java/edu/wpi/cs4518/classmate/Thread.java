package edu.wpi.cs4518.classmate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Thread {
    private int id;
    private String postText;
    private String timestamp;
    private String author;
    private String category;
    private List<Comment> comments;



    public Thread(){}

    public Thread(JSONObject jObject) {
        this.comments = new ArrayList<Comment>();

        try {
            this.id = jObject.getInt("id");
            this.postText = jObject.getString("postText");
            this.timestamp = jObject.getString("timestamp");
            this.author = jObject.getString("author");
            this.category = jObject.getString("category");
            JSONArray jsonArray = jObject.getJSONArray("comments");
            this.comments = new ArrayList<Comment>();

            for (int i=0; i < jsonArray.length(); i++)
            {
                try {
                    System.out.println(i);
                    JSONObject obj = jsonArray.getJSONObject(i);
                    this.comments.add(new Comment(obj));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        };

    }

    public int getId() {return id;}

    public String getPostText() {
        return this.postText;
    }

    public String getAuthor() {
        return this.author;
    }

    public String getCategory() {
        return this.category;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public List<Comment> getComments() {
        return this.comments;
    }

}
