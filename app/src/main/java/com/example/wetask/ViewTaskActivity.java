package com.example.wetask;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import java.util.ArrayList;

public class ViewTaskActivity extends AppCompatActivity {

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

        SharedPreferences sharedPref = this.getSharedPreferences("weTask", MODE_PRIVATE);
        int currGroup = sharedPref.getInt("group", 0);
        toolbar.setTitle(groupNames.get(currGroup));
    }

    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }
}
