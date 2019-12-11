package com.example.wetask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
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
import java.util.Collection;
import java.util.Random;

import static android.view.Menu.NONE;

public class GroupSettings extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference groups, users, tasks;
    private Button complete, leave;
    private EditText edit;
    private String groupId;
    private ArrayList<String> userList;
    private ArrayList<String> globalUserList;
    private ListView userListView;
    private ArrayAdapter<String> userListAdapter;
    //1 if we are editing, 0 if creating new group
    private int editVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_settings);

        database = FirebaseDatabase.getInstance();
        users = database.getReference("users");
        groups = database.getReference("groups");
        tasks = database.getReference("tasks");

        groupId = MainActivity.groupId;
        Intent intent = getIntent();
        editVal = intent.getIntExtra("edit?", -1);

        globalUserList = new ArrayList<>();
        loadGlobalUsers();

        userList = new ArrayList<>();
        loadUsers();

        final AutoCompleteTextView user =  findViewById(R.id.add_user);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                globalUserList);
        user.setAdapter(adapter);

        Button add_button = findViewById(R.id.add_user_button);
        add_button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUser(user.getText().toString());
                user.setText("");
            }
        });

        // THIS IS WHERE I INITIATE THE ADAPTER FOR LIST VIEW
        // THE LIST ITSELF IS FILLED IN LOADUSERS()
        userListView = findViewById(R.id.user_list);
        TextView textView = new TextView(this);
        textView.setText("  Current Members:");
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
        textView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        textView.setTextColor(getResources().getColor(R.color.colorBlack));

        userListView.addHeaderView(textView);
        userListAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1, userList);
        userListView.setAdapter(userListAdapter);

        if (editVal == 1) { //editing
            EditText groupName = findViewById(R.id.edit_group_name);
            groupName.setText(MainActivity.groupName);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        if (editVal == 0) {
            actionBar.setTitle("Add Group");
        } else {
            actionBar.setTitle(MainActivity.groupName);
        }

        complete = findViewById(R.id.confirm_group);
        complete.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit = (EditText) findViewById(R.id.edit_group_name);
                String groupName = edit.getText().toString();
                if (groupName.isEmpty()){ //data validation
                    Toast.makeText(GroupSettings.this, "Error: Empty Group Name", Toast.LENGTH_LONG).show();
                    return;
                }
                String userID = user.getText().toString();
                if (editVal == 0) { //if creating group
                    Random r = new Random();
                    int tag = r.nextInt();
                    String id = Integer.toString(tag);
                    makeNewGroup(id, groupName, userID);
                } else if (editVal == 1) { //if editing group
                    editGroup(MainActivity.groupId, groupName, userID);
                }
            }
        });

        leave = findViewById(R.id.leave_group);
        if(editVal == 0){ //creating group
            leave.setText("CANCEL");
            leave.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v){
                    finish();
                }
            });
        } else { //editing group
            leave.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v){
                    leaveGroup(MainActivity.userId, MainActivity.groupId);
                }
            });
        }
    }

    private void leaveGroup(final String userID, final String groupID){
        if (MainActivity.groupNameList.size() <= 1) {
            Toast.makeText(GroupSettings.this, "Error: Can't Leave Only Group", Toast.LENGTH_LONG).show();
            return;
        }
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //Remove this group from the user's group list
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserObject tempUser = dataSnapshot.child(userID).getValue(UserObject.class);
                tempUser.removeGroup(groupID);
                users.child(userID).setValue(tempUser);

                ArrayList<String> groupIDs = MainActivity.groupIdList;
                ArrayList<String> groupNames = MainActivity.groupNameList;

                String groupId = "";
                String groupName = "";
                if (groupIDs.size() > 1 && groupIDs.indexOf(MainActivity.groupId) + 1 < groupIDs.size()) {
                    groupId = groupIDs.get(groupIDs.indexOf(MainActivity.groupId) + 1);
                    groupName = groupNames.get(groupNames.indexOf(MainActivity.groupName) + 1);
                } else if (groupNames.size() > 1 && groupNames.indexOf(MainActivity.groupName) - 1 >= 0 ) {
                    groupId = groupIDs.get(groupIDs.indexOf(MainActivity.groupId) - 1);
                    groupName = groupNames.get(groupNames.indexOf(MainActivity.groupName) - 1);
                }

                MainActivity.groupName = groupName;
                MainActivity.groupId = groupId;

                SharedPreferences sharedPref = GroupSettings.this.getSharedPreferences("weTask", MODE_PRIVATE);
                String userName = sharedPref.getString("userId", "userName");
                users.child(userName).child("lastGroupAccessed").setValue(groupId);
                users.child(userName).child("lastGroupName").setValue(groupName);

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

    private void loadGlobalUsers(){
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

    private void loadUsers(){
        if (editVal == 0) { //creating group
            userList.add(MainActivity.userId);
        } else { //editing
            groups.addListenerForSingleValueEvent( new ValueEventListener( ) {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    GroupObject group = dataSnapshot.child( groupId ).getValue( GroupObject.class );
                    ArrayList<String> users = group.getGroupUserList( );
                    userList.clear( );
                    userList.addAll( users );
                    userListAdapter.notifyDataSetChanged( );
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            } );
        }
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
                for (String u:userList) {
                    test_group.addUser(u);
                    addGroupToUser(test_group.getGroupID(), u);
                }
                groups.child(finalId).setValue(test_group);
                MainActivity.groupName = name;
                MainActivity.groupId = id;
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void editGroup(final String id, final String newName, final String newUserID) {
        groups.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GroupObject temp = dataSnapshot.child(id).getValue(GroupObject.class);
                ArrayList<String> currentList = temp.getGroupUserList();
                for (String u:userList) {
                    if(!currentList.contains(u)) { //not adding duplicates of users
                        temp.addUser(u);
                        addGroupToUser(id, u);
                    }
                }
                groups.child(id).setValue(temp);
                DatabaseReference groupRef = groups.child(id);
                groupRef.child("groupName").setValue(newName);
                MainActivity.groupName = newName;
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

    public void addUser(final String userID) {
        if (!globalUserList.contains(userID)) {
            Toast.makeText(GroupSettings.this, "Error: Invalid Username", Toast.LENGTH_LONG).show();
            return;
        } else if (userList.contains(userID)) {
            Toast.makeText(GroupSettings.this, "Error: Already in Group", Toast.LENGTH_LONG).show();
            return;
        } else { //valid user
            userList.add(userID);
            userListAdapter.notifyDataSetChanged();
            //adds to group when hitting confirm to save changes
        }
    }
}