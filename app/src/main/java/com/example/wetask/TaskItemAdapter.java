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

        if (tk != null) {

            TextView typeText = (TextView) taskView.findViewById( R.id.transaction_text );
            TextView dateText = (TextView) taskView.findViewById( R.id.date_text );
            TextView amountText = (TextView) taskView.findViewById( R.id.amount_view );

            amountText.setText( items.get( position ).getName( ) );
            //amountText.setText("hi2");
            dateText.setText( "hi" );
            typeText.setText( "hi" );
        }

        return taskView;
    }

    public String getTaskIdAtPos(int pos) {
        return getItem(pos).getTaskId();
    }

    public TaskItem getTaskAtPos(int pos) {return getItem(pos);}


}
