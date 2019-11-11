package com.example.wetask;

import androidx.annotation.NonNull;
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
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.view.Menu.NONE;

public class MainActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference groups, tasks, users;
    private String userId;
    private String groupId = "g100"; // need to figure out how to get group id
    static ArrayList<TaskItem> myTasks;
    static ArrayList<TaskItem> allTasks;
    private ArrayList<TaskItem> archiveTasks;
    private TaskItemAdapter myTaskAdapter;
    private TaskItemAdapter allTaskAdapter;
    private TaskItemAdapter archiveTaskAdapter;
    private ArrayList<TaskItemAdapter> masterList;
    private ArrayList<String> groupNames;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPref = this.getSharedPreferences("weTask", MODE_PRIVATE);
        userId = sharedPref.getString("userID", "N/A");
        groups = FirebaseDatabase.getInstance().getReference("groups");
        tasks = FirebaseDatabase.getInstance().getReference("tasks");
        users = FirebaseDatabase.getInstance().getReference("users");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        setSupportActionBar(toolbar);

        //TODO: get actual names of groups this person is in from database
        groupNames = new ArrayList<String>();
        groupNames.add("Apartment 101"); //dummy data
        groupNames.add("ASPCA Volunteers"); //dummy


        int currGroup = sharedPref.getInt("group", 0);
        //TODO: use currGroup to send correct adapter to SecPagAd for current group

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle(groupNames.get(currGroup));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //CoordinatorLayout drawer = (CoordinatorLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditTaskActivity.class);
                startActivity(intent);
            }
        });

        addMenuItemInNavMenuDrawer(groupNames);

        /** Dummy data for testing layouts--make sure to replace this with real data pulled from database!**/

        masterList = makeDummyData();

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
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        Menu groupMenu = navView.getMenu().findItem(R.id.groupSubmenuHolder).getSubMenu();

        for (int i = 0; i < groupNames.size(); i++) {
            if (item == groupMenu.getItem(i)) {
                //set correct masterList based on new group
                SharedPreferences sharedPref = this.getSharedPreferences("weTask", MODE_PRIVATE);
                SharedPreferences.Editor edit = sharedPref.edit();
                edit.putInt("group", i);
                edit.commit();

                Toolbar toolbar = findViewById(R.id.toolbar);
                toolbar.setTitle(groupNames.get(i));
            }
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

        if (id == R.id.login) {
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
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

        make_dummy_database();
        TaskItem my_item = new TaskItem("mytask", "1", "", "simon");
        TaskItem all_item = new TaskItem("alltask", "2", "", "simon");
        TaskItem archive_item = new TaskItem("archive", "3", "", "simon");

        updateMyTasks();
        updateAllTasks();
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

    /*Programmatically adds groups to nav drawer.**/
    private void addMenuItemInNavMenuDrawer(ArrayList<String> groupNames) {
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        Menu menu = navView.getMenu();
        Menu submenu = menu.findItem(R.id.groupSubmenuHolder).getSubMenu();

        for (int i = 0; i < groupNames.size(); i++) {
            submenu.add(NONE, NONE, 0, groupNames.get(i));
        }
        navView.invalidate();
    }

    private void updateAllTasks(){
        groups.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GroupObject group = dataSnapshot.child(groupId).getValue(GroupObject.class);
                ArrayList<String> tasks = group.getGroupTaskList();
                for(int i = 0; i < tasks.size(); i++){
                    allTask_add(tasks.get(i));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateMyTasks(){
        groups.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GroupObject group = dataSnapshot.child(groupId).getValue(GroupObject.class);
                ArrayList<String> tasks = group.getGroupTaskList();
                for(int i = 0; i < tasks.size(); i++){
                    myTask_add(tasks.get(i), userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void myTask_add(final String taskId, final String userId){
        tasks.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TaskItem task = dataSnapshot.child(taskId).getValue(TaskItem.class);
                if(task.getAssignedTo().equals(userId)) {
                    myTasks.add(task);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void allTask_add(final String taskId){
        tasks.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TaskItem task = dataSnapshot.child(taskId).getValue(TaskItem.class);
                allTasks.add(task);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void make_dummy_database(){
        TaskItem task_1 = new TaskItem("Wash dishes", "t100", "g100", "simon");
        TaskItem task_2 = new TaskItem("Take out trash", "t101", "g100", "simon");
        TaskItem task_3 = new TaskItem("Change Sheets", "t102", "g100", "jacob");

        ArrayList<String> simon_group = new ArrayList<String>();
        ArrayList<String> jacob_group = new ArrayList<String>();
        simon_group.add("g100");
        jacob_group.add("g100");

        UserObject SIMON = new UserObject("simon", simon_group, "123456");
        UserObject JACOB = new UserObject("jacob", jacob_group, "123456");

        GroupObject APARTMENT = new GroupObject("g100", "Apartment101");
        APARTMENT.addUser(SIMON.getUserID());
        APARTMENT.addUser(JACOB.getUserID());
        APARTMENT.addGroupTask(task_1.getTaskId());
        APARTMENT.addGroupTask(task_2.getTaskId());
        APARTMENT.addGroupTask(task_3.getTaskId());

        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        groups.child(APARTMENT.getGroupID()).setValue(APARTMENT);
        mRef.child("tasks").child(task_1.getTaskId()).setValue(task_1);
        mRef.child("tasks").child(task_2.getTaskId()).setValue(task_2);
        mRef.child("tasks").child(task_3.getTaskId()).setValue(task_3);
        mRef.child("users").child(SIMON.getUserID()).setValue(SIMON);
        mRef.child("users").child(JACOB.getUserID()).setValue(JACOB);

    }


    @Override
    public void onResume(){
        super.onResume();
        myTaskAdapter = new TaskItemAdapter(this, R.layout.task_item_layout, myTasks);
        allTaskAdapter = new TaskItemAdapter( this, R.layout.task_item_layout, allTasks );
        masterList.set(0, myTaskAdapter);
        masterList.set(1, allTaskAdapter);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager( ), masterList);
        ViewPager viewPager = findViewById( R.id.view_pager );
        viewPager.setAdapter( sectionsPagerAdapter );
        TabLayout tabs = findViewById( R.id.tabs );
        tabs.setupWithViewPager( viewPager );

    }

}
