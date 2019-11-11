package com.example.wetask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class EditTaskActivity extends AppCompatActivity {
    private GroupObject current_group;
    private UserObject current_user;
    private DatabaseReference groups, tasks, users;
    private boolean if_new;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_edit_task );
        //get_current_user();
        DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");
        tasks = FirebaseDatabase.getInstance().getReference("tasks");
        groups = FirebaseDatabase.getInstance().getReference("groups");
        users = FirebaseDatabase.getInstance().getReference("users");

        get_current_group();
        Intent intent = getIntent();
        if(intent.getIntExtra("if_new", 0) == 0){

        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar( );
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled( true );
        }

        SharedPreferences sharedPref = this.getSharedPreferences("weTask", MODE_PRIVATE);
        String currGroupStr = sharedPref.getString("groupStr", "Group Not Found");
        toolbar.setTitle(currGroupStr);

        TextView taskTitle = findViewById(R.id.taskTitle);
        String title = sharedPref.getString("title", "No Title");
        taskTitle.setText(title);

        TextView create = findViewById(R.id.create);
        final String createdBy = "Created By: " + sharedPref.getString("create", "User Not Found");
        create.setText(createdBy);

        final EditText comment = findViewById(R.id.comments_detail);
        final EditText deadline = findViewById(R.id.deadline_date);
        final EditText create_date = findViewById(R.id.created_date);

        Button button = findViewById(R.id.confirm_changes);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String c_date = create_date.getText().toString();
                String ddl = deadline.getText().toString();
                String com = comment.getText().toString();

                Random r = new Random();
                int tag = r.nextInt();
                String ID = Integer.toString(tag);

                TaskItem new_task = new TaskItem(ID, ID, "g100", c_date, "jacob", "simon", ddl, com);
                tasks.child(new_task.getTaskId()).setValue(new_task);
                current_group.addGroupTask(new_task.getTaskId());
                groups.child("g100").setValue(current_group);
                MainActivity.myTasks.add(new_task);
                MainActivity.allTasks.add(new_task);
                Intent intent = new Intent(EditTaskActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        //add on click listener to confirm changes which either creates task or changes task (and pushes
        //to database) depending on sharedpref mode attribute being edit or create
    }

    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }

    private void get_current_group(){
        groups.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                current_group = dataSnapshot.child("g100").getValue(GroupObject.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

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
}
