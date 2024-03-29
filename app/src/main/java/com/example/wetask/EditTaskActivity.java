package com.example.wetask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EditTaskActivity extends AppCompatActivity{
    private GroupObject current_group;
    private UserObject current_user;
    private DatabaseReference groups, tasks, users;
    private String aTo = "Unassigned";
    private ArrayList<String> users_list;
    private String deadline = "";
    private Button deadline_view;
    private Spinner spinner;
    private String origATo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_edit_task );
        DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");
        tasks = FirebaseDatabase.getInstance().getReference("tasks");
        groups = FirebaseDatabase.getInstance().getReference("groups");
        users = FirebaseDatabase.getInstance().getReference("users");

        get_current_group();

        final Intent intent = getIntent();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar( );
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled( true );
        }

        final SharedPreferences sharedPref = this.getSharedPreferences("weTask", MODE_PRIVATE);
        toolbar.setTitle(MainActivity.groupName);

        final EditText taskTitle = findViewById(R.id.new_task_name);
        deadline_view = findViewById(R.id.deadline_date);
        final EditText comment = findViewById(R.id.comments_edit);

        if (intent.getIntExtra("if_new", 1) == 0) { //EDITING
            taskTitle.setText(sharedPref.getString("title", "No Title"));
            deadline = sharedPref.getString("deadline", "MM/DD/YY");
            deadline_view.setText(deadline);
            comment.setText(sharedPref.getString("comments", ""));
            aTo = sharedPref.getString("assignee", "Unassigned");
            origATo = aTo; //fixes async bug with spinner setup
        }

        spinner = findViewById(R.id.assignee);
        users_list = new ArrayList<String>();
        populate_users_list();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, users_list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                aTo = parent.getItemAtPosition(position).toString();
            }

            public void onNothingSelected(AdapterView<?> parent) {
                aTo = "Unassigned";
            }
        }) ;

        deadline_view.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        Button button = findViewById(R.id.confirm);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = taskTitle.getText().toString();
                if (name.isEmpty()) {
                    Toast.makeText(EditTaskActivity.this, "Error: Empty Task Name", Toast.LENGTH_LONG).show();
                    return;
                }
                String ddl = deadline_view.getText().toString();
                String aBy = "Not Assigned Yet"; //or some other default message
                if (!aTo.equals("Unassigned")) {
                    aBy = sharedPref.getString("userID", "user error");
                }
                String com = comment.getText().toString();

                if((intent.getIntExtra("if_new", 0) == 0)){ //IF EDITING TASK
                    String ID = intent.getStringExtra("taskID");

                    TaskItem task = new TaskItem(name, ID, MainActivity.groupId, aBy, aTo, ddl, com);
                    tasks.child(task.getTaskId()).setValue(task);

                    SharedPreferences.Editor edit = sharedPref.edit();
                    edit.putString("title", task.getName());
                    edit.putString("created", task.getCreatedDate());
                    edit.putString("deadline", task.getDeadline());
                    edit.putString("assigner", task.getAssignedBy());
                    edit.putString("assignee", task.getAssignedTo());
                    edit.putString("comments", task.getComments());
                    edit.putString("taskId", task.getTaskId());
                    edit.commit();

                    for(int i = 0; i < MainActivity.allTasks.size(); i++){
                        if(MainActivity.allTasks.get(i).getTaskId().equals(ID)){
                            MainActivity.allTasks.set(i, task);
                            MainActivity.allTaskAdapter.notifyItemChanged(i);
                        }
                    }
                    boolean updated = false;
                    for(int i = 0; i < MainActivity.myTasks.size(); i++){
                        if(MainActivity.myTasks.get(i).getTaskId().equals(ID)){
                            MainActivity.myTasks.remove(i);
                            MainActivity.myTaskAdapter.notifyItemRemoved(i);
                            if(MainActivity.userId.equals(aTo)){
                                MainActivity.myTasks.add(i, task);
                                MainActivity.myTaskAdapter.notifyItemInserted(i);
                                updated = true;
                            }
                        }
                    }
                    if (MainActivity.userId.equals(aTo) && updated == false) {
                        MainActivity.myTasks.add(task);
                        MainActivity.myTaskAdapter.notifyItemInserted(MainActivity.myTasks.size()-1);
                    }

                } else { //IF CREATING A TASK
                    Random r = new Random();
                    int tag = r.nextInt();
                    String ID = Integer.toString(tag);

                    TaskItem new_task = new TaskItem(name, ID, MainActivity.groupId, aBy, aTo, ddl, com);
                    tasks.child(new_task.getTaskId()).setValue(new_task);
                    current_group.addGroupTask(new_task.getTaskId());
                    groups.child(MainActivity.groupId).setValue(current_group);
                    if(aTo.equals(MainActivity.userId)) {
                        MainActivity.myTasks.add(new_task);
                        int index = MainActivity.myTasks.size();
                        MainActivity.myTaskAdapter.notifyItemInserted(index - 1);
                    }
                    MainActivity.allTasks.add(new_task);
                    int index = MainActivity.allTasks.size();
                    MainActivity.allTaskAdapter.notifyItemInserted(index - 1);
                }
                //MainActivity.notify_changes();
                finish();
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }

    private void get_current_group(){
        groups.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                current_group = dataSnapshot.child( MainActivity.groupId ).getValue( GroupObject.class );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    /*not currently used, but keep so can display pref name not user id*/
    private void get_current_user(){
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                current_user = dataSnapshot.child("simon").getValue(UserObject.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void populate_users_list() {
        users_list.add("Unassigned");
        groups.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> users = current_group.getGroupUserList();
                users_list.addAll(users);
                if (!origATo.isEmpty()) { //fixes async bug if editing
                    aTo = origATo;
                }
                spinner.setSelection(users.indexOf(aTo) + 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }

        });
    }

    public void datePicked(String ddl) {
        deadline = ddl;
        deadline_view.setText(deadline);
    }
}
