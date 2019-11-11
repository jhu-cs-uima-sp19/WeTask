package com.example.wetask;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 */
public class TaskItemAdapter extends ArrayAdapter<TaskItem> {

    private int resource;
    private List<TaskItem> items;


    public TaskItemAdapter(Context ctx, int res, List<TaskItem> items)
    {
        super(ctx, res, items);
        resource = res;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout taskView;
        TaskItem tk = getItem(position);


        if (convertView == null) {
            taskView = new LinearLayout( getContext( ) );
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater vi = (LayoutInflater) getContext( ).getSystemService( inflater );
            vi.inflate( resource, taskView, true );
        } else {
            taskView = (LinearLayout) convertView;
        }

        TextView titleText = (TextView) taskView.findViewById( R.id.task_title );
        TextView createdText = (TextView) taskView.findViewById( R.id.created );
        TextView assignedText = (TextView) taskView.findViewById( R.id.assigned_to );

        titleText.setText(items.get(position).getName());
        createdText.setText(items.get(position).getCreatedDate());
        assignedText.setText(items.get(position).getAssignedTo());

        return taskView;
    }

    public String getTaskIdAtPos(int pos) {
        return getItem(pos).getTaskId();
    }

    public TaskItem getTaskAtPos(int pos) {return getItem(pos);}


}
