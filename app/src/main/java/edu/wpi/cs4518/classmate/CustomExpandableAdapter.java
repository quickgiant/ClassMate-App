package edu.wpi.cs4518.classmate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class CustomExpandableAdapter extends BaseExpandableListAdapter {
    private Context _context;
    private List<Thread> mListDataHeader; // header titles
    // child data in format of thread, comment list
    private HashMap<Thread, List<Comment>> mListDataChild;

    public CustomExpandableAdapter(Context context, List<Thread> listDataHeader,
                                 HashMap<Thread, List<Comment>> listChildData) {
        this._context = context;
        this.mListDataHeader = listDataHeader;
        this.mListDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.mListDataChild.get(this.mListDataHeader.get(groupPosition)).get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        // getting comment data for the row
        final Comment c = (Comment) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.discussion_list_row_comment, null);
        }

        TextView commentText = (TextView) convertView.findViewById(R.id.commentText);
        TextView commentTimestamp = (TextView) convertView.findViewById(R.id.commentTimestamp);
        TextView commentAuthor = (TextView) convertView.findViewById(R.id.commentAuthor);

        // thread_commentText
        commentText.setText(c.getCommentText());

        // thread_author
        String authortext = "By: " + String.valueOf(c.getAuthor());
        commentAuthor.setText(authortext);

        // timestamp
        commentTimestamp.setText(c.getTimestamp());

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        System.err.println("-----------------------------------------");
        System.err.println(this.mListDataHeader.get(groupPosition).getPostText());
        System.err.println(this.mListDataHeader.get(groupPosition).getComments());
        System.err.println("-----------------------------------------");

        return this.mListDataChild.get(this.mListDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.mListDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.mListDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        Thread t = (Thread) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.discussion_list_row, null);
        }

        TextView postText = (TextView) convertView.findViewById(R.id.postText);
        TextView author = (TextView) convertView.findViewById(R.id.author);
        TextView category = (TextView) convertView.findViewById(R.id.category);
        TextView timestamp = (TextView) convertView.findViewById(R.id.timestamp);

        postText.setText(t.getPostText());
        String authortext = "By: " + String.valueOf(t.getAuthor());
        author.setText(authortext);
        category.setText(t.getCategory());
        timestamp.setText(t.getTimestamp());


        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


}
