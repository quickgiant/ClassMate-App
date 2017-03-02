package edu.wpi.cs4518.classmate;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NavUtils;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class DiscussionActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String url = "http://104.131.102.232/forum/threads/";
    private ProgressDialog pDialog;
    private List<Thread> listDataHeader;
    private HashMap<Thread, List<Comment>> listDataChild;
    private ExpandableListView listView;
    private CustomExpandableAdapter adapter;
    private RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion);

        // Navigation
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Initialize thread list, comment hashmap, listview
        listDataHeader = new ArrayList<Thread>();
        listDataChild = new HashMap<Thread, List<Comment>>();

        // Set ListView adapter for expandable list
        listView = (ExpandableListView) findViewById(R.id.list);
        adapter = new CustomExpandableAdapter(this, listDataHeader, listDataChild);
        listView.setAdapter(adapter);

        // Show progress dialog before making http request
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        retrieveThreads();

        // Listview Group click listener
        // Occurs on expand & collapse
        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                // Toast.makeText(getApplicationContext(),
                // "Group Clicked " + listDataHeader.get(groupPosition),
                // Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // Listview Group expanded listener
        // Shows a toast
        listView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        listDataHeader.get(groupPosition) + " Expanded",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Listview Group collasped listener
        // Shows a toast
        listView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        listDataHeader.get(groupPosition) + " Collapsed",
                        Toast.LENGTH_SHORT).show();

            }
        });

        // Listview on child click listener
        // Opens AlertDialog for adding (or removing, if thread_author names match)
        // a given comment from a thread
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, final int childPosition, long id) {
                /*
                Toast.makeText(
                        getApplicationContext(),
                        listDataHeader.get(groupPosition).getId()
                                + " : "
                                + listDataChild.get(
                                listDataHeader.get(groupPosition)).get(
                                childPosition).hashCode(), Toast.LENGTH_SHORT)
                        .show();
                */
                AlertDialog.Builder b1 = new AlertDialog.Builder(DiscussionActivity.this);
                b1.setMessage("Comment Options");
                b1.setCancelable(true);
                final int groupP = groupPosition;
                final int childP = childPosition;

                // Show an add comment button
                b1.setPositiveButton("Add Comment", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        addCommentsDialog(groupP, childP);
                        dialog.cancel();
                    }
                });

                // Show a remove button if the user owns the comment
                if( listDataChild.get(listDataHeader.get(groupPosition)).get(
                        childPosition).getAuthor().equals(
                        listDataHeader.get(groupPosition).getAuthor()) ){
                    b1.setNegativeButton("Remove Comment", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                }

                AlertDialog a1 = b1.create();
                a1.show();

                return false;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;

        if (id == R.id.nav_scheduler) {
            // Switch to scheduler
            intent = new Intent(this, SchedulerActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_map) {
            // Switch to discussion
            intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Hides the progress bar
    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    public void retrieveThreads() {
        // Make JSON request to URL, add threads to list, map threads to
        // lists of comments in the hashmap (this can be changed to Strings/Integers)
        JsonArrayRequest threadReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        hidePDialog();
                        listDataHeader.clear();
                        listDataChild.clear();

                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject obj = response.getJSONObject(i);
                                Thread thread = new Thread(obj);
                                listDataHeader.add(thread);
                                listDataChild.put(thread, thread.getComments());

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hidePDialog();

            }
        });

        // Make volley request to update the threads
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        mRequestQueue.add(threadReq);
    }

    // Function for adding Comments
    // This will make POST calls
    public boolean addCommentsDialog(final int groupP, int childP) {
        AlertDialog.Builder b1 = new AlertDialog.Builder(DiscussionActivity.this);
        b1.setMessage("Write Comment");
        b1.setTitle("Title Comment");

        b1.setCancelable(true);
        final EditText edittext = new EditText(DiscussionActivity.this);
        b1.setView(edittext);
        b1.setPositiveButton("Add Comment", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String etv = edittext.getText().toString();
                Thread t = listDataHeader.get(groupP);
                sendComment(etv, t);
                dialog.cancel();
            }
        });

        AlertDialog a1 = b1.create();
        a1.show();
        return false;
    }

    public void sendComment(final String comment, Thread thread) {
        StringRequest postCommentRequest = new StringRequest(Request.Method.POST,
                "http://104.131.102.232/forum/threads/" + thread.getId() + "/comment",
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        Toast.makeText(getApplicationContext(), "Comment added", Toast.LENGTH_SHORT)
                                .show();
                        retrieveThreads();
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        VolleyLog.e("VolleyEventServerResponse", "Error: " + error.getMessage());
                    }
                }) {

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("commentText", comment);
                params.put("author", "ClassMate");
                return params;
            };
        };
        mRequestQueue.add(postCommentRequest);
    }
}
