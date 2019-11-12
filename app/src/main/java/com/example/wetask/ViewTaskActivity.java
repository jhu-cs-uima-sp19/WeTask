package com.example.wetask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.acl.Group;
import java.util.ArrayList;

public class ViewTaskActivity extends AppCompatActivity {
    private DatabaseReference groups;

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

        final SharedPreferences sharedPref = this.getSharedPreferences("weTask", MODE_PRIVATE);
        String currGroupStr = sharedPref.getString("groupStr", "Group Not Found");
        toolbar.setTitle(currGroupStr);

        TextView taskTitle = findViewById(R.id.taskTitle);
        String title = sharedPref.getString("title", "No Title");
        taskTitle.setText(title);

        TextView create = findViewById(R.id.create);
        String createdBy = "Created: " + sharedPref.getString("created", "1/1/2020");
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

        Button complete = findViewById(R.id.complete);
        complete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                sharedPref.getString("taskId", "");
                delete_task();
/*                TaskItem task = new TaskItem(); //get that task from database;
                task.complete();
                //put it back in database
                finish();*/
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.view_task_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences sharedPref = this.getSharedPreferences("weTask", MODE_PRIVATE);
        switch (item.getItemId()) {
            case R.id.edit_task:
                Intent intent = new Intent(ViewTaskActivity.this, EditTaskActivity.class);
                intent.putExtra("if_new", 0);
                intent.putExtra("taskID", sharedPref.getString("taskId", ""));
                startActivity(intent);
                return true;

            case R.id.delete_task:
                delete_task();
                /*DatabaseReference tasks = FirebaseDatabase.getInstance().getReference("tasks");
                String tag = sharedPref.getString("taskId", "");
                tasks.child(tag).removeValue();
                String groupID = MainActivity.groupId;
                groups = FirebaseDatabase.getInstance().getReference("groups");
                remove_task_from_group(tag, groupID);

                for(int i = 0; i < MainActivity.allTasks.size(); i++){
                    if(MainActivity.allTasks.get(i).getTaskId().equals(tag)){
                        MainActivity.allTasks.remove(i);
                    }
                }
                for(int i = 0; i < MainActivity.myTasks.size(); i++){
                    if(MainActivity.myTasks.get(i).getTaskId().equals(tag)){
                        MainActivity.myTasks.remove(i);
                    }
                }
                MainActivity.notify_changes();
                finish();*/
                return true;

            case android.R.id.home:
                finish();

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void remove_task_from_group(final String taskID, final String groupID){
        groups.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GroupObject g = dataSnapshot.child(groupID).getValue(GroupObject.class);
                g.removeGroupTask(taskID);
                groups.child(groupID).setValue(g);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void delete_task() {
        SharedPreferences sharedPref = this.getSharedPreferences("weTask", MODE_PRIVATE);

        DatabaseReference tasks = FirebaseDatabase.getInstance().getReference("tasks");
        String tag = sharedPref.getString("taskId", "");
        tasks.child(tag).removeValue();
        String groupID = MainActivity.groupId;
        groups = FirebaseDatabase.getInstance().getReference("groups");
        remove_task_from_group(tag, groupID);

        for(int i = 0; i < MainActivity.allTasks.size(); i++){
            if(MainActivity.allTasks.get(i).getTaskId().equals(tag)){
                MainActivity.allTasks.remove(i);
            }
        }
        for(int i = 0; i < MainActivity.myTasks.size(); i++){
            if(MainActivity.myTasks.get(i).getTaskId().equals(tag)){
                MainActivity.myTasks.remove(i);
            }
        }
        MainActivity.notify_changes();
        finish();
    }


}
