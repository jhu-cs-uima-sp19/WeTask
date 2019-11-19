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

import static android.view.Menu.NONE;

public class MainActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference groups, tasks, users;
    static String userId;
    static String groupId = "g100"; // need to figure out how to get group id
    static ArrayList<TaskItem> myTasks;
    static ArrayList<TaskItem> allTasks;
    static ArrayList<TaskItem> archiveTasks;
    static TaskItemAdapter myTaskAdapter;
    static TaskItemAdapter allTaskAdapter;
    static TaskItemAdapter archiveTaskAdapter;
    private ArrayList<String> groupNames;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPref = this.getSharedPreferences("weTask", MODE_PRIVATE);
        userId = sharedPref.getString("userID", "N/A");
        groups = FirebaseDatabase.getInstance().getReference("groups");
        tasks = FirebaseDatabase.getInstance().getReference("tasks");
        users = FirebaseDatabase.getInstance().getReference("users");
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);

        //TODO: get actual names of groups this person is in from database
        groupNames = new ArrayList<String>();
        groupNames.add("Apartment 101"); //dummy data
        groupNames.add("ASPCA Volunteers"); //dummy

        int currGroup = sharedPref.getInt("group", 0);
        //TODO: use currGroup to send correct adapter to SecPagAd for current group

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle(groupNames.get(currGroup));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
                intent.putExtra("if_new", 1);
                startActivity(intent);
            }
        });

        addMenuItemInNavMenuDrawer(groupNames);

        makeDummyData();

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
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
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void makeDummyData() {
        myTasks = new ArrayList<TaskItem>();
        allTasks = new ArrayList<TaskItem>();
        archiveTasks = new ArrayList<TaskItem>();

        updateMyTasks();
        updateAllTasks();
        updateArchivedTasks();
        Collections.sort(myTasks, new TaskComparator());
        Collections.sort(allTasks, new TaskComparator());
        Collections.sort(archiveTasks, new TaskComparator());

        myTaskAdapter = new TaskItemAdapter(this, R.layout.task_item_layout, myTasks);
        allTaskAdapter = new TaskItemAdapter( this, R.layout.task_item_layout, allTasks );
        archiveTaskAdapter = new TaskItemAdapter( this, R.layout.task_item_layout, archiveTasks );
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
                        myTasks.add( task );
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
                    allTaskAdapter.notifyDataSetChanged( );
                }
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

    public static void notify_changes(){
        Collections.sort(myTasks, new TaskComparator());
        Collections.sort(allTasks, new TaskComparator());
        Collections.sort(archiveTasks, new TaskComparator());
        myTaskAdapter.notifyDataSetChanged();
        allTaskAdapter.notifyDataSetChanged();
        archiveTaskAdapter.notifyDataSetChanged();
    }

    public static class TaskComparator implements Comparator<TaskItem> {
        @Override
        public int compare(TaskItem task_1, TaskItem task_2){
            if(task_1.getAssignedTo().equals(" ")){
                if(!task_2.getAssignedTo().equals(" ")) {  // if task1 is not assigned but task2 is assigned
                    return 1;
                }
            }else{
                if(task_2.getAssignedTo().equals(" ")){  //if task1 is assigned but task2 is not assigned
                    return -1;
                }
            }
            SimpleDateFormat formatter = new SimpleDateFormat("MM/DD/YY");
            String date1 = task_1.getDeadline();
            String date2 = task_2.getDeadline();
            try {
                return formatter.parse(date1).compareTo(formatter.parse(date2));
            } catch (ParseException e) {
                e.printStackTrace();
                return 0;
            }
        }
    }
}
