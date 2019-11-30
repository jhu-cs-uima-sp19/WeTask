package com.example.wetask;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

import static android.content.Context.MODE_PRIVATE;

/**
 */
public class TaskItemAdapter extends RecyclerView.Adapter<TaskHolder> {

    private int resource;
    private List<TaskItem> items;
    private Context context;


/*    public TaskItemAdapter(Context ctx, int res, List<TaskItem> items)
    {
        //super(ctx, res, items);
        this.context = ctx;
        resource = res;
        this.items = items;
    }*/

    public TaskItemAdapter(List<TaskItem> items) {
        this.items = items;
        //this.resource = res;
        this.resource = R.layout.task_item_layout;
    }

    // 2. Override the onCreateViewHolder method
    @Override
    public TaskHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // 3. Inflate the view and return the new ViewHolder
        View view = LayoutInflater.from(parent.getContext())
                .inflate(this.resource, parent, false);
        return new TaskHolder(view);
    }

    // 4. Override the onBindViewHolder method
    @Override
    public void onBindViewHolder(TaskHolder holder, int position) {

        // 5. Use position to access the correct Bakery object
        TaskItem task = this.items.get(position);

        // 6. Bind the bakery object to the holder
        holder.bindTask(task);
    }

    @Override
    public int getItemCount() {

        return this.items.size();
    }

    /*@Override
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
        TextView deadlineText = (TextView) taskView.findViewById( R.id.due );
        TextView assignedText = (TextView) taskView.findViewById( R.id.assigned_to );

        titleText.setText(items.get(position).getName());
        deadlineText.setText(items.get(position).getDeadline());
        assignedText.setText(items.get(position).getAssignedTo());

        return taskView;
    }

    public String getTaskIdAtPos(int pos) {
        return getItem(pos).getTaskId();
    }

    public TaskItem getTaskAtPos(int pos) {return getItem(pos);}
*/

}
