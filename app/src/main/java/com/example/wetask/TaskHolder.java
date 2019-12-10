package com.example.wetask;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wetask.R;
import com.example.wetask.TaskItem;

import androidx.recyclerview.widget.RecyclerView;

import static android.content.Context.MODE_PRIVATE;

public class TaskHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final TextView title;
    private final TextView assignee;
    private final TextView deadline;

    private TaskItem task;
    private Context context;

    public TaskHolder(View itemView) {

        super(itemView);

        // 1. Set the context
        this.context = itemView.getContext();

        // 2. Set up the UI widgets of the holder
        this.title = (TextView) itemView.findViewById(R.id.task_title);
        this.assignee = (TextView) itemView.findViewById(R.id.assigned_to);
        this.deadline = (TextView) itemView.findViewById(R.id.due);

        // 3. Set the "onClick" listener of the holder
        itemView.setOnClickListener(this);
    }

    public void bindTask(TaskItem taskItem) {

        // 4. Bind the data to the ViewHolder
        this.task = taskItem;
        this.title.setText(task.getName());
        this.assignee.setText(task.getAssignedTo());
        this.deadline.setText(task.getDeadline());
    }

    @Override
    public void onClick(View v) {

        SharedPreferences sharedPref = context.getSharedPreferences("weTask", MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPref.edit();
        edit.putString("title", task.getName());
        edit.putString("created", task.getCreatedDate());
        edit.putString("deadline", task.getDeadline());
        edit.putString("assigner", task.getAssignedBy());
        edit.putString("assignee", task.getAssignedTo());
        edit.putString("comments", task.getComments());
        edit.putString("taskId", task.getTaskId());
        edit.putBoolean("finished", task.ifFinished());
        edit.commit();

        Intent intent = new Intent(context, ViewTaskActivity.class);
        intent.putExtra("if_new", 0);
        context.startActivity(intent);
    }
}