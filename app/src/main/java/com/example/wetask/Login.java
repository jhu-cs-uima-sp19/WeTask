package com.example.wetask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.example.wetask.MainActivity.groupId;

public class Login extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference users, groups;
    EditText edtUsername, edtPassword;
    Button login, signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.login);

        database = FirebaseDatabase.getInstance();
        users = database.getReference("users");
        groups = database.getReference("groups");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        login = findViewById(R.id.login);
        signup = findViewById(R.id.signup);
        edtUsername = (EditText) findViewById(R.id.emailUsername);
        edtPassword = (EditText) findViewById(R.id.password);

        login.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Login.this, "verifying...", Toast.LENGTH_SHORT).show();
                signIn(edtUsername.getText().toString(), edtPassword.getText().toString());
            }
        });

        signup.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Login.this, "Signing you up...", Toast.LENGTH_SHORT).show();
                signUp(edtUsername.getText().toString(), edtPassword.getText().toString());
            }
        });
    }

    private void signIn(final String username, final String password){
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(username).exists()){
                    UserObject login = dataSnapshot.child(username).getValue(UserObject.class);
                    if(login.getPassword().equals(password)){
                        SharedPreferences sharedPref = getSharedPreferences("weTask", MODE_PRIVATE);
                        SharedPreferences.Editor edit = sharedPref.edit();
                        edit.putString("userID", username);
                        edit.commit();
                        //get_first_group(username);
                        Intent main = new Intent(Login.this, MainActivity.class);
                        startActivity(main);
                    }else{
                        Toast.makeText(Login.this, "Wrong username/password combination", Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(Login.this, "User doesn't exist", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void signUp(final String username, final String password){
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.child(username).exists()) {
                    ArrayList<String> test_list = new ArrayList<String>();
                    UserObject test_user = new UserObject(username, test_list, password);

                    Random r = new Random();
                    String newGroupID = Integer.toString(r.nextInt());
                    String newGroupName = username+"_private";
                    GroupObject newGroup = new GroupObject(newGroupID, newGroupName);
                    newGroup.addUser(username);
                    test_user.addGroup(newGroupID);

                    users.child(username).setValue(test_user);
                    groups.child(newGroupID).setValue(newGroup);

                    Toast.makeText(Login.this, "Registered", Toast.LENGTH_LONG).show();
                    SharedPreferences sharedPref = getSharedPreferences("weTask", MODE_PRIVATE);
                    SharedPreferences.Editor edit = sharedPref.edit();
                    edit.putString("userID", username);
                    edit.putString("groupID", newGroupID);
                    edit.commit();
                    //get_first_group(username);
                    Intent main = new Intent(Login.this, MainActivity.class);
                    startActivity(main);
                } else {
                    Toast.makeText(Login.this, "This username already exists", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //This isn't used for accessing group on login, as we want to land on last group accessed
    //But i'm leaving it because it might be useful for signup, or could be altered to act as a
    //failsafe for if the group in sharedPref no longer exists.
    private void get_first_group(final String userId){
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserObject temp = dataSnapshot.child(userId).getValue(UserObject.class);
                ArrayList<String> groups = temp.getGroupList();
                String firstGroup = "";
                if (groups.size() != 0) {
                    firstGroup = groups.get(0);
                }
//                Log.d("LAUNCH_GETGROUP", firstGroup);
                SharedPreferences sharedPref = getSharedPreferences("weTask", MODE_PRIVATE);
                SharedPreferences.Editor edit = sharedPref.edit();
                if (groups.size() != 0) {
                    edit.putString("groupID", firstGroup);
                }
                edit.commit();
                Intent main = new Intent(Login.this, MainActivity.class);
                startActivity(main);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void onBackPressed() {
    }
}

