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
import android.renderscript.Sampler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static android.view.Menu.NONE;

public class MainActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference groups, tasks, users;
    static ArrayList<String> groupNameList;
    static ArrayList <String> groupIdList;
    static String userId;
    static String groupId = "0"; // need to figure out how to get group id
    static String groupName = "0";
    static ArrayList<TaskItem> myTasks;
    static ArrayList<TaskItem> allTasks;
    static ArrayList<TaskItem> archiveTasks;
    static TaskItemAdapter myTaskAdapter;
    static TaskItemAdapter allTaskAdapter;
    static TaskItemAdapter archiveTaskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*initialize database instances and get group to start in*/
        users = FirebaseDatabase.getInstance().getReference("users");
        groups = FirebaseDatabase.getInstance().getReference("groups");
        tasks = FirebaseDatabase.getInstance().getReference("tasks");

        final SharedPreferences sharedPref = this.getSharedPreferences("weTask", MODE_PRIVATE);
        userId = sharedPref.getString("userID", "N/A");
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserObject user = dataSnapshot.child(userId).getValue(UserObject.class);
                try {
                    groupId = user.getLastGroupAccessed();
                    groupName = user.getLastGroupName();
                } catch (NullPointerException e) {
                    //nothing needs to be here
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        groups.addListenerForSingleValueEvent( new ValueEventListener( ) {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GroupObject group = dataSnapshot.child(groupId).getValue(GroupObject.class);
                groupName = group.getGroupName();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        } );

        /*enable hamburger icon nav drawer ability*/
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        /*set up welcome message in nav drawer*/
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View navHeader = navigationView.getHeaderView(0);
        TextView navMessage = navHeader.findViewById(R.id.profile_info);
        navMessage.setText("Welcome, "+userId+"!");

        /*builds functionality to launch add task into FAB*/
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditTaskActivity.class);
                intent.putExtra("if_new", 1);
                startActivity(intent);
            }
        });

        /*initializing public task lists and adapters*/
        myTasks = new ArrayList<TaskItem>();
        allTasks = new ArrayList<TaskItem>();
        archiveTasks = new ArrayList<TaskItem>();
        myTaskAdapter = new TaskItemAdapter(myTasks);
        allTaskAdapter = new TaskItemAdapter(allTasks);
        archiveTaskAdapter = new TaskItemAdapter(archiveTasks);
        update_task_lists();

        /*setting up pager adapter to set up tabs*/
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById( R.id.view_pager );
        viewPager.setAdapter( sectionsPagerAdapter );
        TabLayout tabs = findViewById( R.id.tabs );
        tabs.setupWithViewPager( viewPager );
    }

    public void onResume() {
        super.onResume();
        initializeGroupLists(); //and nav menu drawer
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
                intent.putExtra("edit?", 1);
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

    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        String title = item.getTitle().toString();

        SharedPreferences sharedPref = this.getSharedPreferences("weTask", MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPref.edit();

        if (groupNameList.contains(title)) {
            //set locally
            groupName = title;
            groupId = groupIdList.get(groupNameList.indexOf(groupName));

            //set in sharedprefs to pass to other activities (which is frankly unnecessary, but whatever)
            edit.putString("groupName",groupName);
            edit.putString("groupID",groupId);
            edit.apply();

            //add to user
            users.child(userId).child("lastGroupAccessed").setValue(groupId);
            users.child(userId).child("lastGroupName").setValue(groupName);
            update_task_lists(); //updating task lists for new group**************

            //updating toolbar
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle(groupName);
        } else if (id == R.id.nav_add_group) {
            Intent intent = new Intent(MainActivity.this, GroupSettings.class);
            intent.putExtra("edit?", 0);
            startActivity(intent);
        } else if (id == R.id.logout) {
            finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void addGroupsToNavMenuDrawer() {

        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        Menu menu = navView.getMenu();
        Menu submenu = menu.findItem(R.id.groupSubmenuHolder).getSubMenu();
        submenu.clear();
        for (String group : groupNameList) {
            submenu.add(group);
        }
        navView.invalidate();
    }

    private void initializeGroupLists() {
        groups.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groupNameList = new ArrayList<String>();
                groupIdList = new ArrayList<String>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GroupObject group = snapshot.getValue(GroupObject.class);
                    if(group.getGroupUserList().contains(userId)){ //check if current user has this group
                        groupNameList.add(group.getGroupName());
                        groupIdList.add(group.getGroupID());
                        if (group.getGroupID() == groupId) { //fixes bug with another group member editing
                            groupName = group.getGroupName(); //last group user was in
                            //TODO: take out if can fix another way
                        }
                    }
                }
                addGroupsToNavMenuDrawer();
                Toolbar toolbar = findViewById(R.id.toolbar);
                toolbar.setTitle(groupName);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    /**Below are methods to initialize lists and handle data changes.*/

    /*Called when need to fill in task lists from scratch: on group switch or when app starts.*/
    public void update_task_lists() {
        Log.d( "CALL", "UPDATING TASK LISTS" );
        myTasks.clear( );
        allTasks.clear( );
        archiveTasks.clear( );

        updateMyTasks( );
        updateAllTasks( );
        updateArchivedTasks( );

        myTaskAdapter.notifyDataSetChanged();
        allTaskAdapter.notifyDataSetChanged();
        archiveTaskAdapter.notifyDataSetChanged();
    }

    private void updateArchivedTasks(){
        groups.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GroupObject group = dataSnapshot.child(groupId).getValue(GroupObject.class);
                ArrayList<String> temp = group.getArchivedTaskList();
                if(temp != null) {
                    for (int i = 0; i < temp.size(); i++) {
                        archiveTask_add(temp.get(i));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
                if (task != null) {
                    if (task.getAssignedTo( ).equals( userId )) {
                        Log.d("ADD", "Adding to myTask");
                        myTasks.add( task );
                        Log.d("ADD_LENGTH", "");
                    }
                    myTaskAdapter.notifyDataSetChanged( );
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
                if (task != null) {
                    allTasks.add( task );
                }
                allTaskAdapter.notifyDataSetChanged( );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void archiveTask_add(final String taskId){
        tasks.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TaskItem task = dataSnapshot.child(taskId).getValue(TaskItem.class);
                task.complete();
                if(task != null){
                    archiveTasks.add(task);
                    archiveTaskAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }
}
