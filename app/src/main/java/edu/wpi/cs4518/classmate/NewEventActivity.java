package edu.wpi.cs4518.classmate;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import static android.R.attr.onClick;

public class NewEventActivity extends AppCompatActivity {

    RequestQueue mRequestQueue;
    GregorianCalendar mDateCalendar;
    Button mDateView;
    SimpleDateFormat mDateFormat;
    int mStartTimeHour;
    int mStartTimeMinute;
    int mEndTimeHour;
    int mEndTimeMinute;
    Button mStartTimeView;
    Button mEndTimeView;
    Place mPlace;

    int PLACE_PICKER_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        // Show toolbar title and back button
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.new_event);

        // Initialize date and time buttons
        initializeDateButton();
        initializeTimeButtons();

        Button addMapLocationButton = (Button) findViewById(R.id.add_map_location_button);
        addMapLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(NewEventActivity.this), PLACE_PICKER_REQUEST);
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }
        });

        if(mRequestQueue == null){
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
    }

    public void initializeDateButton() {
        // Fill date text view with today's date
        mDateView = (Button) findViewById(R.id.date_view);
        mDateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy");
        mDateView.setText(mDateFormat.format(new Date()));

        // Define inner class for the date picker
        @SuppressLint("ValidFragment")
        class DatePickerFragment extends DialogFragment
                implements DatePickerDialog.OnDateSetListener {

            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                // Use the current date as the default date in the picker
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // Create a new instance of DatePickerDialog and return it
                return new DatePickerDialog(getActivity(), this, year, month, day);
            }

            public void onDateSet(DatePicker view, int year, int month, int day) {
                // Do something with the date chosen by the user
                mDateCalendar = new GregorianCalendar(year, month, day);
                mDateView.setText(mDateFormat.format(mDateCalendar.getTime()));
            }
        }

        // Set click behavior to show date picker
        mDateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });
    }

    public void initializeTimeButtons() {
        // Define inner class for the time picker
        @SuppressLint("ValidFragment")
        class TimePickerFragment extends DialogFragment
                implements TimePickerDialog.OnTimeSetListener {

            String mId;

            public TimePickerFragment(String id) {
                super();
                mId = id;
            }

            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                // Use the current time as the default values for the picker
                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                // Create a new instance of TimePickerDialog and return it
                return new TimePickerDialog(getActivity(), this, hour, minute,
                        DateFormat.is24HourFormat(getActivity()));
            }

            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String timeString = "";
                if(hourOfDay == 0 || hourOfDay == 12) {
                    timeString += "12";
                }
                else {
                    timeString += hourOfDay % 12;
                }
                timeString += ":" + String.format("%02d", minute);
                timeString += hourOfDay < 12 ? " AM" : " PM";

                if(mId.equals("startTime")) {
                    mStartTimeHour = hourOfDay;
                    mStartTimeMinute = minute;
                    mStartTimeView.setText(timeString);
                }
                else if(mId.equals("endTime")) {
                    mEndTimeHour = hourOfDay;
                    mEndTimeMinute = minute;
                    mEndTimeView.setText(timeString);
                }
            }
        }

        mStartTimeView = (Button) findViewById(R.id.start_time_view);
        mEndTimeView = (Button) findViewById(R.id.end_time_view);
        // Set click behaviors to show time picker
        mStartTimeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePickerFragment = new TimePickerFragment("startTime");
                timePickerFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });
        mEndTimeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePickerFragment = new TimePickerFragment("endTime");
                timePickerFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.new_event_action_bar, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        else if(item.getItemId() == R.id.done) {
            sendEvent();
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                mPlace = PlacePicker.getPlace(data, this);
                Toast.makeText(getApplicationContext(), "Map location added successfully", Toast.LENGTH_SHORT)
                        .show();
            }
            else {
                Toast.makeText(getApplicationContext(), "Map location selection failed", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    public void sendEvent() {
        final String title = ((EditText)findViewById(R.id.title_field)).getText().toString();
        final String semanticLocation = ((EditText)findViewById(R.id.semantic_location_field)).getText().toString();
        final double latitude;
        final double longitude;
        if(mPlace != null) {
            latitude = mPlace.getLatLng().latitude;
            longitude = mPlace.getLatLng().longitude;
        }
        else {
            LatLng wpiDefault = new LatLng(42.274469, -71.807770);
            latitude = wpiDefault.latitude;
            longitude = wpiDefault.longitude;
        }

        final GregorianCalendar startTimeCalendar = (GregorianCalendar) mDateCalendar.clone();
        startTimeCalendar.set(Calendar.HOUR, mStartTimeHour);
        startTimeCalendar.set(Calendar.MINUTE, mStartTimeMinute);

        final GregorianCalendar endTimeCalendar = (GregorianCalendar) mDateCalendar.clone();
        endTimeCalendar.set(Calendar.HOUR, mEndTimeHour);
        endTimeCalendar.set(Calendar.MINUTE, mEndTimeMinute);

        if(endTimeCalendar.compareTo(startTimeCalendar) < 0) {
            endTimeCalendar.roll(Calendar.DAY_OF_MONTH, true);
        }

        StringRequest myReq = new StringRequest(Request.Method.POST,
                "http://104.131.102.232/events",
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        Toast.makeText(getApplicationContext(), "Event added", Toast.LENGTH_SHORT)
                                .show();
                        NavUtils.navigateUpFromSameTask(NewEventActivity.this);
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
                params.put("title", title);
                params.put("semanticLocation", semanticLocation);
                params.put("latitude", Double.toString(latitude));
                params.put("longitude", Double.toString(longitude));
                params.put("usersAttending", "0");
                params.put("startTime", Long.toString(startTimeCalendar.getTimeInMillis()));
                params.put("endTime", Long.toString(endTimeCalendar.getTimeInMillis()));
                return params;
            };
        };

        mRequestQueue.add(myReq);
    }
}
