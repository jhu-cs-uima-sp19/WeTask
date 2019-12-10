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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

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
    private HashMap<Integer, String> current_groupID_list = new HashMap<Integer, String>();
    private HashMap<Integer, String> current_groupName_list = new HashMap<Integer, String>();
    static String userId;
    static String groupId = "0"; // need to figure out how to get group id
    static int groupPos;
    static String groupName = "0";
    static String userName;
    static ArrayList<TaskItem> myTasks;
    static ArrayList<TaskItem> allTasks;
    static ArrayList<TaskItem> archiveTasks;
    static TaskItemAdapter myTaskAdapter;
    static TaskItemAdapter allTaskAdapter;
    static TaskItemAdapter archiveTaskAdapter;
    //static ArrayList<String> groupNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("LAUNCH", "MAIN ACTIVITY LAUNCH");
        setContentView(R.layout.activity_main);

        /*initialize database instances and get group to start in*/


        users = FirebaseDatabase.getInstance().getReference("users");
        groups = FirebaseDatabase.getInstance().getReference("groups");
        tasks = FirebaseDatabase.getInstance().getReference("tasks");

        final SharedPreferences sharedPref = this.getSharedPreferences("weTask", MODE_PRIVATE);
        final SharedPreferences.Editor edit = sharedPref.edit();
        userId = sharedPref.getString("userID", "N/A");
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserObject user = dataSnapshot.child(userId).getValue(UserObject.class);
                try {
                    groupId = user.getLastGroupAccessed();
                    groupName = user.getLastGroupName();
                    edit.putString("groupName", groupName);
                    Log.d("Name", groupName);
                    edit.apply();
                } catch (NullPointerException e) {
                    groupId = sharedPref.getString("groupID", "n/a");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });




        groupPos = sharedPref.getInt("groupPos", 0);
        userName = sharedPref.getString("userID", "N/A");
        Log.d("from shared pref", groupId);

        /*enable hamburger icon nav drawer ability*/
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (!groupId.equals("N/A")) {
            toolbar.setTitle(current_groupName_list.get(groupId));
        }
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
        Log.d("Resume", "Resume MainActivity");
        super.onResume();
        addMenuItemInNavMenuDrawer();
        update_toolbar();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_buttons, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences sharedPref = this.getSharedPreferences("weTask",MODE_PRIVATE);
        SharedPreferences.Editor  edit = sharedPref.edit();
        switch (item.getItemId()) {
            case R.id.group_settings:
                /*
                Intent intent = new Intent(MainActivity.this, GroupSettings.class);
                //put extra with current group name (editing not creating)
                intent.putExtra("groupID", groupId);
                intent.putExtra("groupName",current_groupName_list.get(groupPos));
                intent.putExtra("edit?", 1);
                startActivity(intent);
                return true; */
                Intent intent = new Intent(MainActivity.this, GroupSettings.class);
                //put extra with current group name (editing not creating)
                edit.apply();
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

    //@Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        SharedPreferences sharedPref = this.getSharedPreferences("weTask", MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPref.edit();
        groupPos = id;
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        Menu groupMenu = navView.getMenu().findItem(R.id.groupSubmenuHolder).getSubMenu();

//        for (int i = 0; i < groupNames.size(); i++) {
//            if (item == groupMenu.getItem(i)) {
//                //set correct masterList based on new group
//                SharedPreferences sharedPref = this.getSharedPreferences("weTask", MODE_PRIVATE);
//                SharedPreferences.Editor edit = sharedPref.edit();
//                edit.putInt("group", i);
//                edit.commit();
//
//                Toolbar toolbar = findViewById(R.id.toolbar);
//                toolbar.setTitle(groupNames.get(i));
//            }
//        }

        if(current_groupID_list.keySet().contains(id)){
            // Update groupID and fragments
            groupId = current_groupID_list.get(id);
            groupName = current_groupName_list.get(id);
            edit.putString("groupName",groupName);
            edit.putString("groupID",groupId);
            users.child(userName).child("lastGroupAccessed").setValue(groupId);
            users.child(userName).child("lastGroupName").setValue(groupName);
            edit.apply();
            Log.d("from shared pref", groupId);
            Log.d("SWITCHGROUP", Integer.toString(id));
            Log.d("SWITCHGROUP", "GROUP SWITCHED");
            Log.d("SWITCHGROUP", groupId);
            update_task_lists(); //updating task lists for new group**************
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle(current_groupName_list.get(id));


        }

        if (id == R.id.nav_add_group) {
            Intent intent = new Intent(MainActivity.this, GroupSettings.class);
            //put extra with group name empty (populating new group)
            intent.putExtra("edit?", 0);
            startActivity(intent);
        }

        if (id == R.id.logout) {
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void addMenuItemInNavMenuDrawer() {
        Log.d("MENU", "UPDATE MENU");
        current_groupID_list = new HashMap<Integer, String>();
        current_groupName_list = new HashMap<Integer, String>();

        groups.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
                Menu menu = navView.getMenu();
                Menu submenu = menu.findItem(R.id.groupSubmenuHolder).getSubMenu();
                submenu.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GroupObject group = snapshot.getValue(GroupObject.class);
                    String groupName = group.getGroupName();
                    //check if current user has this group
                    if(!group.getGroupUserList().contains(userId)){
                        continue;
                    }

                    Log.d("MENU", groupName);

                    Random r = new Random();
                    MenuItem temp = submenu.add(NONE, r.nextInt(), 0, groupName);
                    current_groupID_list.put(temp.getItemId(), group.getGroupID()); //store list of groupID for switching between groups
                    current_groupName_list.put(temp.getItemId(), groupName);
                    Log.d("GROUPID", Integer.toString(temp.getItemId()));
                    Log.d("GROUPID", group.getGroupID());
                }
                navView.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void update_toolbar(){
        groups.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GroupObject group = dataSnapshot.child(groupId).getValue(GroupObject.class);
                String name = group.getGroupName();
                Toolbar toolbar = findViewById(R.id.toolbar);
                toolbar.setTitle(name);
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

    /**NEEDS UPDATE BEFORE USING AGAIN--currently too slow & unwieldy, need to sort, figure out where
     * single item went, and then use adapter.notifyItemMoved() method for fast & reliable behavior
     */
/*    public static void notify_changes(){
        Collections.sort(myTasks, new TaskComparator());
        Collections.sort(allTasks, new TaskComparator());
        Collections.sort(archiveTasks, new TaskComparator());
        myTaskAdapter.notifyDataSetChanged();
        allTaskAdapter.notifyDataSetChanged();
        archiveTaskAdapter.notifyDataSetChanged();
    }*/

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
