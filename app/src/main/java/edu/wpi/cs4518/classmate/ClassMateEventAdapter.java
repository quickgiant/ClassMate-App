package edu.wpi.cs4518.classmate;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ClassMateEventAdapter extends ArrayAdapter<ClassMateEvent> {
    public ClassMateEventAdapter(Context context, ArrayList<ClassMateEvent> eventArray) {
        super(context, R.layout.event_list_item, eventArray);
    }

    @Override
    public @NonNull View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if(convertView != null) {
            view = convertView;
        }
        else {
            view = View.inflate(getContext(), R.layout.event_list_item, parent);
        }
        TextView eventTitleView = (TextView) view.findViewById(R.id.event_title);
        TextView dateView = (TextView) view.findViewById(R.id.date_view);
        TextView timeView = (TextView) view.findViewById(R.id.time_view);
        TextView usersAttendingView = (TextView) view.findViewById(R.id.users_attending_view);
        TextView semanticLocationView = (TextView) view.findViewById(R.id.semantic_location_view);

        ClassMateEvent event = getItem(position);
        if(event != null) {
            eventTitleView.setText(event.getTitle());
            dateView.setText(event.getDateString());
            timeView.setText(event.getTimeString());
            usersAttendingView.setText(event.getUsersAttending());
            semanticLocationView.setText(event.getSemanticLocation());
        }
        else {
            throw new NullPointerException("ClassMateEventAdapter missing event object at" +
            " position " + position);
        }
        return view;
    }
}
