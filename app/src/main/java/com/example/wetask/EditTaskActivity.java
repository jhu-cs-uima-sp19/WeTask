package com.example.wetask;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

public class EditTaskActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_edit_task );

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
        String createdBy = "Created By: " + sharedPref.getString("create", "User Not Found");
        create.setText(createdBy);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }
}
