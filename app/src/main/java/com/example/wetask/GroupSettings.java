package com.example.wetask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
    DatabaseReference groups, users;
    Button complete;
    EditText edit;
    //1 if we are editing, 0 if creating new group
    int editVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_settings);

        Intent intent = getIntent();
        editVal = intent.getIntExtra("edit?", -1);

        if (editVal == 1) {
            EditText groupName = findViewById(R.id.edit_group_name);
            groupName.setText(intent.getStringExtra("groupName"));
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        SharedPreferences sharedPref = this.getSharedPreferences("weTask", MODE_PRIVATE);

        database = FirebaseDatabase.getInstance();
        users = database.getReference("users");
        groups = database.getReference("groups");

        complete = findViewById(R.id.confirm_group);
        complete.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit = (EditText) findViewById(R.id.edit_group_name);
                EditText user = (EditText) findViewById(R.id.add_user);
                String groupName = edit.getText().toString();
                String userID = user.getText().toString();

                if (editVal == 0) { //if creating group
                    Random r = new Random();
                    int tag = r.nextInt();
                    String id = Integer.toString(tag);
                    makeNewGroup(id, groupName, userID);
                } else if (editVal == 1) { //if editing group
                    Intent intent = getIntent();
                    String id = intent.getStringExtra("groupID");
                    editGroup(id, groupName, userID);
                }
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
        groups.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GroupObject temp = dataSnapshot.child(id).getValue(GroupObject.class);
                if(!newUserID.isEmpty()){
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