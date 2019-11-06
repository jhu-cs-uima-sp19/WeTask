package com.example.wetask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wetask.main.SectionsPagerAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {

    private ListView taskList;
    private TaskItemAdapter aa;
    private TaskItemAdapter bb;
    private DatabaseReference mDatabase;


    // this is temporarily set up with package and static access so
    // that job detail can get access items --
    // would be changed to private instance if supported by
    // permanent back-end database
    private ArrayList<TaskItem> taskItems1;
    private ArrayList<TaskItem> taskItems2;

    private ArrayList<TaskItem> myTasks;
    private ArrayList<TaskItem> allTasks;
    private TaskItemAdapter myTaskAdapter;
    private TaskItemAdapter allTaskAdapter;
    private TaskItemAdapter archiveTaskAdapter;
    private ArrayList<TaskItemAdapter> masterList;
    private ListView myList;

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

        taskList = (ListView) findViewById(R.id.taskItems);
        // create ArrayList of courses from database
        taskItems1 = new ArrayList<TaskItem>();
        taskItems2 = new ArrayList<TaskItem>();

        myTasks = new ArrayList<TaskItem>();
        allTasks = new ArrayList<TaskItem>();

        TaskItem my_item = new TaskItem("mytask", "1");
        TaskItem all_item = new TaskItem("alltask", "2");

        myTasks.add(my_item);
        myTasks.add(my_item);
        myTasks.add(my_item);
        allTasks.add(all_item);
        allTasks.add(all_item);

        myTaskAdapter = new TaskItemAdapter(this, R.layout.task_item_layout, myTasks);
        allTaskAdapter = new TaskItemAdapter( this, R.layout.task_item_layout, allTasks );
        archiveTaskAdapter = new TaskItemAdapter( this, R.layout.task_item_layout, taskItems1 );

        masterList = new ArrayList<TaskItemAdapter>();
        masterList.add(myTaskAdapter);
        masterList.add(allTaskAdapter);
        masterList.add(archiveTaskAdapter);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager( ), masterList);
        ViewPager viewPager = findViewById( R.id.view_pager );
        viewPager.setAdapter( sectionsPagerAdapter );
        TabLayout tabs = findViewById( R.id.tabs );
        tabs.setupWithViewPager( viewPager );

        //myList = (ListView) findViewById(R.id.myTaskItems);
        //myList.setAdapter(myTaskAdapter);
/*
        // make array adapter to bind arraylist to listview with new custom item layout
        aa = new TaskItemAdapter(this, R.layout.task_item_layout, taskItems1);
        bb = new TaskItemAdapter(this, R.layout.task_item_layout, taskItems2);
        int currGroup = sharedPref.getInt("group", 1);

        switch (currGroup) {
            case 2:
                //taskList.setAdapter(bb);
                break;
            default:
                //taskList.setAdapter(aa);
        }
*/
/*        TaskItem item = new TaskItem();
        TaskItem add_task_item = new TaskItem("ADD TASK", "1");
        taskItems1.add(0, add_task_item);
        taskItems1.add(1, item);
        taskItems2.add(0, add_task_item);
        taskItems2.add(1, item);
        taskItems2.add(2, item);
        taskItems2.add(3, item);*/
        //dummy list for testing putting in tabs
        /*TaskItem my_item = new TaskItem("mytask", "1");
        TaskItem all_item = new TaskItem("alltask", "2");

        myTasks.add(my_item);
        myTasks.add(my_item);
        myTasks.add(my_item);
        allTasks.add(all_item);
        allTasks.add(all_item);*/

/*        final TaskItem newItem = new TaskItem("newItem", "2");
        taskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Snackbar.make(view, "Selected #" + id, Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                if(position == 0){
                    String id_new = newItem.getTaskId();
                    mDatabase.child("tasks").child(id_new).setValue(newItem);
                }
            }
        });*/
        //final TaskItem newItem = new TaskItem("newItem", "2");
        /*taskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Snackbar.make(view, "Selected #" + id, Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                if(position == 0){
                    String id_new = newItem.getTaskId();
                    mDatabase.child("tasks").child(id_new).setValue(newItem);
                }
            }
        });*/


    }

    @Override
    protected void onStart()
    {
        super.onStart();
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
            taskList.setAdapter(aa);
            SharedPreferences sharedPref = this.getSharedPreferences("weTask", MODE_PRIVATE);
            SharedPreferences.Editor edit = sharedPref.edit();
            edit.putInt("group", 1);
            edit.commit();

        }

        if (id == R.id.group2) {
            taskList.setAdapter(bb);
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

}
