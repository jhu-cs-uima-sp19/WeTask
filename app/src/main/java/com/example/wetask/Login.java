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

import java.util.ArrayList;
import java.util.List;

public class Login extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference users;
    EditText edtUsername, edtPassword;
    Button login, signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.login);

        database = FirebaseDatabase.getInstance();
        users = database.getReference("users");
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
                    List<String> test_list = new ArrayList<String>();
                    UserObject test_user = new UserObject("simon", test_list, password);
                    users.child(username).setValue(test_user);
                    Toast.makeText(Login.this, "Registered", Toast.LENGTH_LONG).show();
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

}

