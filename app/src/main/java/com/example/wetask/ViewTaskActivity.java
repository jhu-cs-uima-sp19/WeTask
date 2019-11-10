package com.example.wetask;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;

public class ViewTaskActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_view_task );

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar( );
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled( true );
        }

        //TODO: get actual names of groups this person is in from database
        //or just group this task is in--store string groupname in sharedpref?
        ArrayList<String> groupNames = new ArrayList<String>();
        groupNames.add("Apartment 101"); //dummy data
        groupNames.add("ASPCA Volunteers"); //dummy data

        SharedPreferences sharedPref = this.getSharedPreferences("weTask", MODE_PRIVATE);
        String currGroupStr = sharedPref.getString("groupStr", "Group Not Found");
        toolbar.setTitle(currGroupStr);

        TextView taskTitle = findViewById(R.id.taskTitle);
        String title = sharedPref.getString("title", "No Title");
        taskTitle.setText(title);

        TextView create = findViewById(R.id.create);
        String createdBy = "Created By: " + sharedPref.getString("create", "User Not Found");
        create.setText(createdBy);

        TextView deadline = findViewById(R.id.deadline);
        String deadlineStr = "Deadline: " + sharedPref.getString("deadline", "1/1/2020");
        deadline.setText(deadlineStr);

        TextView assignedBy = findViewById(R.id.assignedBy);
        String assigner = "Assigned By: " + sharedPref.getString("assigner", "User Not Found");
        assignedBy.setText(assigner);

        TextView assignedTo = findViewById(R.id.assignedTo);
        String assignee = "Assigned To: " + sharedPref.getString("assignee", "User Not Found");
        assignedTo.setText(assignee);

        TextView comments = findViewById(R.id.comments);
        String commentStr = "Comments: " + sharedPref.getString("comments", " ");
        comments.setText(commentStr);
    }

//    public boolean onOptionsItemSelected(MenuItem item){
//        finish();
//        return true;
//    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.view_task_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_task:
                Intent intent = new Intent(ViewTaskActivity.this, EditTaskActivity.class);
                //put extra with current group name (editing not creating)
                startActivity(intent);
                return true;

            case R.id.delete_task:

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


}
