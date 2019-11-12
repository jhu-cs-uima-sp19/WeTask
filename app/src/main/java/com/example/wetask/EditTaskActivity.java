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
        final EditText create_date = findViewById(R.id.created_date);
        final EditText deadline = findViewById(R.id.deadline_date);
        final EditText assigner = findViewById(R.id.assigner);
        final EditText assignee = findViewById(R.id.assignee);
        final EditText comment = findViewById(R.id.comments_edit);


        if (intent.getIntExtra("if_new", 0) == 0) {
            taskTitle.setText(sharedPref.getString("title", "No Title"));
            create_date.setText(sharedPref.getString("create", "1/1/2020"));
            deadline.setText(sharedPref.getString("deadline", "1/1/2020"));
            assigner.setText(sharedPref.getString("assigner", "User Not Found"));
            assignee.setText(sharedPref.getString("assignee", "User Not Found"));
            comment.setText(sharedPref.getString("comments", ""));
        }

        Button button = findViewById(R.id.confirm );
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((intent.getIntExtra("if_new", 0) == 0)){ //IF EDITING TASK
                    String ID = intent.getStringExtra("taskID");
                    String name = taskTitle.getText().toString();
                    String c_date = create_date.getText().toString();
                    String aBy = assigner.getText().toString();
                    String aTo = assignee.getText().toString();
                    String ddl = deadline.getText().toString();
                    String com = comment.getText().toString();


                    TaskItem new_task = new TaskItem(name, ID, "g100", c_date, aBy, aTo, ddl, com);
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
                            if(MainActivity.userId.equals(assignee)){
                                MainActivity.myTasks.add(new_task);
                            }
                        }
                    }

                    MainActivity.notify_changes();
                    Intent intent = new Intent(EditTaskActivity.this, MainActivity.class);
                    startActivity(intent);

                } else { //IF CREATING A TASK
                    String name = taskTitle.getText().toString();
                    String c_date = create_date.getText().toString();
                    String aBy = assigner.getText().toString();
                    String aTo = assignee.getText().toString();
                    String ddl = deadline.getText().toString();
                    String com = comment.getText().toString();

                    Random r = new Random();
                    int tag = r.nextInt();
                    String ID = Integer.toString(tag);

                    TaskItem new_task = new TaskItem(name, ID, "g100", c_date, aBy, aTo, ddl, com);
                    tasks.child(new_task.getTaskId()).setValue(new_task);
                    current_group.addGroupTask(new_task.getTaskId());
                    groups.child("g100").setValue(current_group);
                    MainActivity.myTasks.add(new_task);
                    MainActivity.allTasks.add(new_task);
                    MainActivity.notify_changes();
                    Intent intent = new Intent(EditTaskActivity.this, MainActivity.class);
                    startActivity(intent);
                }

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
