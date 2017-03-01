package edu.wpi.cs4518.classmate;

import android.content.Context;
import android.content.Intent;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class EventAdapter extends ArrayAdapter<Event> {
    public EventAdapter(Context context, List<Event> eventArray) {
        super(context, R.layout.event_list_item, eventArray);
    }

    @Override
    public @NonNull View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if(convertView != null) {
            view = convertView;
        }
        else {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.event_list_item, parent, false);
        }
        TextView eventTitleView = (TextView) view.findViewById(R.id.event_title);
        TextView dateView = (TextView) view.findViewById(R.id.date_view);
        TextView timeView = (TextView) view.findViewById(R.id.time_view);
        TextView usersAttendingView = (TextView) view.findViewById(R.id.users_attending_view);
        TextView semanticLocationView = (TextView) view.findViewById(R.id.semantic_location_view);

        final Event event = getItem(position);
        if(event != null) {
            eventTitleView.setText(event.getTitle());
            dateView.setText(event.getDateString());
            timeView.setText(event.getTimeString());
            usersAttendingView.setText(String.format(getContext().getString(R.string.users_attending), event.getUsersAttending()));
            semanticLocationView.setText(event.getSemanticLocation());
        }
        else {
            throw new NullPointerException("EventAdapter missing event object at" +
            " position " + position);
        }

        Button addToCalendarButton = (Button) view.findViewById(R.id.add_to_calendar_button_view);
        addToCalendarButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                handleAddToCalendarClick(event);
            }
        });
        Button attendingButton = (Button) view.findViewById(R.id.attending_button_view);
        attendingButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                handleAttendingClick(event);
            }
        });

        return view;
    }

    public void handleAddToCalendarClick(Event event) {
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, event.getStartTime().getTime())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, event.getEndTime().getTime())
                .putExtra(CalendarContract.Events.TITLE, event.getTitle())
                .putExtra(CalendarContract.Events.EVENT_LOCATION, event.getSemanticLocation());
        getContext().startActivity(intent);
    }

    public void handleAttendingClick(Event event) {
        // TODO: Increment attending and submit to server
    }
}
