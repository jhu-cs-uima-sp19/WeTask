package com.example.wetask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Random;

import static android.view.Menu.NONE;

public class GroupSettings extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference groups, users, tasks;
    Button complete, leave;
    EditText edit;
    String groupId;
    ListView userListView;
    //1 if we are editing, 0 if creating new group
    int editVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_settings);

        database = FirebaseDatabase.getInstance();
        users = database.getReference("users");
        groups = database.getReference("groups");
        tasks = database.getReference("tasks");

        final SharedPreferences sharedPref = this.getSharedPreferences("weTask", MODE_PRIVATE);

        groupId = sharedPref.getString("groupID", "n/a");

        ArrayList<String> globalUserList = new ArrayList<>();
        loadGlobalUsers(globalUserList);

        ArrayList<String> userList = new ArrayList<>();
        loadUsers(userList);


        final AutoCompleteTextView user =  findViewById(R.id.add_user);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                globalUserList);
        user.setAdapter(adapter);
        Intent intent = getIntent();
        editVal = intent.getIntExtra("edit?", -1);


        // THIS IS WHERE I INITIATE THE ADAPTER FOR LIST VIEW
        // THE LIST ITSELF IS FILLED IN LOADUSERS()
        userListView = findViewById(R.id.userList);
        ArrayAdapter<String> userAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1, userList);
        userListView.setAdapter(userAdapter);


        if (editVal == 1) {
            Log.d("groupName",sharedPref.getString("groupName", "N/A"));
            EditText groupName = findViewById(R.id.edit_group_name);
            groupName.setText(sharedPref.getString("groupName", "N/A"));
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //SharedPreferences sharedPref = this.getSharedPreferences("weTask", MODE_PRIVATE);

//        database = FirebaseDatabase.getInstance();
//        users = database.getReference("users");
//        groups = database.getReference("groups");
//        tasks = database.getReference("tasks");

        complete = findViewById(R.id.confirm_group);
        complete.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("EDITGROUP","Complete Clicked");
                Log.d("EDITGROUP",Integer.toString(editVal));
                edit = (EditText) findViewById(R.id.edit_group_name);
                //AutoCompleteTextView user =  findViewById(R.id.add_user);
                String groupName = edit.getText().toString();
                String userID = user.getText().toString();

                if (editVal == 0) { //if creating group
                    Random r = new Random();
                    int tag = r.nextInt();
                    String id = Integer.toString(tag);
                    makeNewGroup(id, groupName, userID);
                } else if (editVal == 1) { //if editing group
                    String id = sharedPref.getString("groupID","0000");
                    Log.d("EDITGROUP",id);
                    Log.d("EDITGROUP",Boolean.toString(userID.isEmpty()));
                    editGroup(id, groupName, userID);
                }
            }
        });

        leave = findViewById(R.id.leave_group);
        if(editVal == 0){
            leave.setVisibility(View.GONE);
        }else {
            leave.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v){
                    String GID = sharedPref.getString("groupID","0000");
                    leaveGroup(MainActivity.userId, GID);
                    //SystemClock.sleep(500); //Force wait for firebase update
                }
            });
        }

    }



    private void leaveGroup(final String userID, final String groupID){
        //Remove this group from the user's group list
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserObject tempUser = dataSnapshot.child(userID).getValue(UserObject.class);
                tempUser.removeGroup(groupID);
                users.child(userID).setValue(tempUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //Mark related tasks as unassigned
        groups.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GroupObject tempGroup = dataSnapshot.child(groupID).getValue(GroupObject.class);
                for(String task: tempGroup.getGroupTaskList()){
                    unassignTask(task, userID);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Remove this user from the group's user list
        groups.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GroupObject tempGroup = dataSnapshot.child(groupID).getValue(GroupObject.class);
                tempGroup.removeUser(userID);
                groups.child(groupID).setValue(tempGroup);
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void unassignTask(final String taskID, final String userID){
        tasks.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TaskItem tempTask = dataSnapshot.child(taskID).getValue(TaskItem.class);
                if(tempTask.getAssignedTo().equals(userID)){
                    tempTask.unassign();
                    tasks.child(taskID).setValue(tempTask);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadGlobalUsers(final ArrayList<String> globalUserList){
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserObject user = snapshot.getValue(UserObject.class);
                    String userId = user.getUserID();
                    globalUserList.add(userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadUsers(final ArrayList<String> userList){
        groups.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GroupObject group = dataSnapshot.child(groupId).getValue(GroupObject.class);
                ArrayList<String> users = group.getGroupUserList();
                userList.clear();
                userList.addAll(users);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void makeNewGroup(final String id, final String name, final String userID) {
        groups.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String finalId = id;
                while (dataSnapshot.child(finalId).exists()) {
                    Random r = new Random();
                    int tag = r.nextInt();
                    finalId = Integer.toString(tag);
                }

                GroupObject test_group = new GroupObject(finalId, name);
                test_group.addUser(MainActivity.userId);
                addGroupToUser(test_group.getGroupID(), MainActivity.userId);

                if (!userID.isEmpty()) {
                    test_group.addUser(userID);
                    addGroupToUser(test_group.getGroupID(), userID);
                }

                groups.child(finalId).setValue(test_group);
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });
    }

    private void editGroup(final String id, final String newName, final String newUserID) {
        Log.d("EDITGROUP",newUserID);
        groups.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GroupObject temp = dataSnapshot.child(id).getValue(GroupObject.class);
                if(!newUserID.isEmpty()){
                    Log.d("EDITGROUP",newUserID);
                    temp.addUser(newUserID);
                    addGroupToUser(id, newUserID);
                }
                groups.child(id).setValue(temp);
                DatabaseReference groupRef = groups.child(id);
                groupRef.child("groupName").setValue(newName);
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });
    }

    private void addGroupToUser(final String groupID, final String userID){
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserObject curr_user = dataSnapshot.child(userID).getValue(UserObject.class);
                curr_user.addGroup(groupID);
                Log.d("USERID", userID);
                users.child(userID).setValue(curr_user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

}