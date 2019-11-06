package com.example.wetask;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference mDatabase;

    private ArrayList<TaskItem> myTasks;
    private ArrayList<TaskItem> allTasks;
    private ArrayList<TaskItem> archiveTasks;
    private TaskItemAdapter myTaskAdapter;
    private TaskItemAdapter allTaskAdapter;
    private TaskItemAdapter archiveTaskAdapter;
    private ArrayList<TaskItemAdapter> masterList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences sharedPref = this.getSharedPreferences("weTask", MODE_PRIVATE);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //CoordinatorLayout drawer = (CoordinatorLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /** Dummy data for testing layouts--make sure to replace this with real data pulled from database!**/
        masterList = makeDummyData();

        int currGroup = sharedPref.getInt("group", 1);
        //TODO: use currGroup to send correct adapter to SecPagAd for current group

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager( ), masterList);
        ViewPager viewPager = findViewById( R.id.view_pager );
        viewPager.setAdapter( sectionsPagerAdapter );
        TabLayout tabs = findViewById( R.id.tabs );
        tabs.setupWithViewPager( viewPager );
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_buttons, menu);
        return true;
    }

   @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.group_settings:
                Intent intent = new Intent(MainActivity.this, GroupSettings.class);
                //put extra with current group name (editing not creating)
                startActivity(intent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen( GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //@Override
    public boolean onNavigationItemSelected(MenuItem item) {//Could make a list of lists of 3 lists of taskItems(3 for personal, all, and archive).
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.group1) {
            //taskList.setAdapter(aa);
            SharedPreferences sharedPref = this.getSharedPreferences("weTask", MODE_PRIVATE);
            SharedPreferences.Editor edit = sharedPref.edit();
            edit.putInt("group", 1);
            edit.commit();

        }

        if (id == R.id.group2) {
            //taskList.setAdapter(bb);
            SharedPreferences sharedPref = this.getSharedPreferences("weTask", MODE_PRIVATE);
            SharedPreferences.Editor edit = sharedPref.edit();
            edit.putInt("group", 2);
            edit.commit();
        }

        if (id == R.id.nav_add_group) {
            Intent intent = new Intent(MainActivity.this, GroupSettings.class);
            //put extra with group name empty (populating new group)
            startActivity(intent);
        }

        if (id == R.id.settings) {
            Intent intent = new Intent(MainActivity.this, AppSettings.class);
            startActivity(intent);
        }

        if (id == R.id.logout) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Can't log you out right now", Toast.LENGTH_SHORT);
            toast.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        //savedInstanceState.putDouble("balance", balance_value);
        //esavedInstanceState.putDouble("input_amount", curr_input);
    }

    public ArrayList<TaskItemAdapter> makeDummyData () {
        myTasks = new ArrayList<TaskItem>();
        allTasks = new ArrayList<TaskItem>();
        archiveTasks = new ArrayList<TaskItem>();

        TaskItem my_item = new TaskItem("mytask", "1");
        TaskItem all_item = new TaskItem("alltask", "2");
        TaskItem archive_item = new TaskItem("archive", "3");

        myTasks.add(my_item);
        myTasks.add(my_item);
        myTasks.add(my_item);
        allTasks.add(all_item);
        allTasks.add(all_item);
        archiveTasks.add(archive_item);

        myTaskAdapter = new TaskItemAdapter(this, R.layout.task_item_layout, myTasks);
        allTaskAdapter = new TaskItemAdapter( this, R.layout.task_item_layout, allTasks );
        archiveTaskAdapter = new TaskItemAdapter( this, R.layout.task_item_layout, archiveTasks );

        masterList = new ArrayList<TaskItemAdapter>();
        masterList.add(myTaskAdapter);
        masterList.add(allTaskAdapter);
        masterList.add(archiveTaskAdapter);
        return masterList;
    }

}
