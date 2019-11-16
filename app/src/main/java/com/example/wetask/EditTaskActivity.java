package com.example.wetask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

public class EditTaskActivity extends AppCompatActivity {
    private GroupObject current_group;
    private UserObject current_user;
    private DatabaseReference groups, tasks, users;
    private static String aTo = "";
    private static ArrayList<String> users_list;
    private static ArrayAdapter<String> adapter;
    static String deadline = "";
    static TextView deadline_view;

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
        String currGroupStr = sharedPref.getString("groupStr", "Group Not Found");
        toolbar.setTitle(currGroupStr);

        final EditText taskTitle = findViewById(R.id.new_task_name);
        deadline_view = findViewById(R.id.deadline_date);
        final EditText comment = findViewById(R.id.comments_edit);

        Spinner spinner = (Spinner) findViewById(R.id.assignee);
        users_list = new ArrayList<String>();
        populate_users_list();

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, users_list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                aTo = (String) parent.getItemAtPosition(position);
            }

            public void onNothingSelected(AdapterView<?> parent) {
                aTo = "Unassigned";
            }
        }) ;

        if (intent.getIntExtra("if_new", 1) == 0) { //EDITING
            taskTitle.setText(sharedPref.getString("title", "No Title"));
            deadline = sharedPref.getString("deadline", "MM/DD/YY");
            deadline_view.setText(deadline);
            comment.setText(sharedPref.getString("comments", ""));
            //TODO: make spinner start with right thing selected if task has already been assigned
        } else {
            deadline = "";
        }

        //UNDER CONSTRUCTION
        ImageButton launch_date_picker = findViewById(R.id.launch_date_picker);
        launch_date_picker.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
                //deadline_view.setText(deadline);
            }
        });

        Button button = findViewById(R.id.confirm );
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = taskTitle.getText().toString();
                String aBy = "Not Assigned Yet"; //or some other default message
                if (!aTo.equals("Unassigned")) {
                    aBy = sharedPref.getString("userID", "user error");
                }
                String com = comment.getText().toString();

                if((intent.getIntExtra("if_new", 0) == 0)){ //IF EDITING TASK
                    String ID = intent.getStringExtra("taskID");

                    TaskItem new_task = new TaskItem(name, ID, "g100", aBy, aTo, deadline, com);
                    tasks.child(new_task.getTaskId()).setValue(new_task);
                    for(int i = 1; i < MainActivity.allTasks.size(); i++){
                        if(MainActivity.allTasks.get(i).getTaskId().equals(ID)){
                            MainActivity.allTasks.remove(i);
                            MainActivity.allTasks.add(new_task);
                        }
                    }
                    for(int i = 1; i < MainActivity.myTasks.size(); i++){
                        if(MainActivity.myTasks.get(i).getTaskId().equals(ID)){
                            MainActivity.myTasks.remove(i);
                            if(MainActivity.userId.equals(aTo)){
                                MainActivity.myTasks.add(new_task);
                            }
                        }
                    }
                    //also, updating created date every time
                } else { //IF CREATING A TASK
                    Random r = new Random();
                    int tag = r.nextInt();
                    String ID = Integer.toString(tag);

                    TaskItem new_task = new TaskItem(name, ID, "g100", aBy, aTo, deadline, com);
                    tasks.child(new_task.getTaskId()).setValue(new_task);
                    current_group.addGroupTask(new_task.getTaskId());
                    groups.child("g100").setValue(current_group);
                    MainActivity.myTasks.add(new_task);
                    MainActivity.allTasks.add(new_task);
                }
                MainActivity.notify_changes();
                Intent intent = new Intent(EditTaskActivity.this, MainActivity.class);
                startActivity(intent);
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
        //TODO: replace with list of users in current group (Simon/ThA)
        users_list.add("Unassigned");
        users_list.add("Sadie");
        users_list.add("Zoe");
        users_list.add("simon");
        users_list.add("a");
    }
}
