package com.example.wetask;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link TabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TabFragment extends Fragment {

    private static final String TAB = "tab";

    //private int tab;
    private String groupId = "g100"; // need to figure out how to get group id
    private ArrayList<TaskItem> tasks;
    private static TaskItemAdapter adapter;

    public TabFragment() {
        // Required empty public constructor
    }

    private TabFragment(int tab) {
        //this.tab = tab;
        //tasks = new ArrayList<TaskItem>();
    }

    /**
     * @param tab is whether this should be MyTasks, AllTasks, or Archive.
     *            list itself is pulled from database and filtered based on tab
     *            adapter is created here
     * @return A new instance of fragment TabFragment.
     */
    public static TabFragment newInstance(int tab) {
        TabFragment fragment = new TabFragment();
        Bundle args = new Bundle( );
        args.putInt(TAB, tab);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate( R.layout.fragment_tab, container, false );
        ListView list = (ListView) view.findViewById(R.id.taskList);
        tasks = new ArrayList<TaskItem>();
        adapter = new TaskItemAdapter(getActivity(), R.layout.task_item_layout, tasks);

//        int tab = savedInstanceState.getInt(TAB, 0);
        int tab = 0;
        if (getArguments( ) != null) {
            tab = getArguments( ).getInt(TAB);
        }
        Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                    ""+tab, Toast.LENGTH_SHORT);
        toast.show();

        switch (tab) {
            case 0:
                //tasks = MainActivity.myTasks;
                adapter = MainActivity.myTaskAdapter;
                break;
            case 1:
                //tasks = MainActivity.allTasks;
                adapter = MainActivity.allTaskAdapter;
                break;
            case 2:
                //tasks = MainActivity.archiveTasks;
                adapter = MainActivity.archiveTaskAdapter;
                break;
        }

        /*DatabaseReference groups = FirebaseDatabase.getInstance().getReference("groups");
        groups.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                GroupObject group = dataSnapshot.child(groupId).getValue(GroupObject.class);
                ArrayList<String> tasksStr = group.getGroupTaskList();

                Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                        ""+tasksStr.size(), Toast.LENGTH_SHORT);
                toast.show();

                for(int i = 0; i < tasksStr.size(); i++){
                    tasks.add(dataSnapshot.child(tasksStr.get(i)).getValue(TaskItem.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
*/

        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //TaskItem selected = tasks.get(position);
                TaskItem selected = adapter.getItem(position);

                SharedPreferences sharedPref = getActivity().getSharedPreferences("weTask", MODE_PRIVATE);
                SharedPreferences.Editor edit = sharedPref.edit();
                edit.putString("title", selected.getName());
                edit.putString("created", selected.getCreatedDate());
                edit.putString("deadline", selected.getDeadline());
                edit.putString("assigner", selected.getAssignedBy());
                edit.putString("assignee", selected.getAssignedTo());
                edit.putString("comments", selected.getComments());
                edit.putString("taskId", selected.getTaskId());
                edit.commit();

                Intent intent = new Intent(getActivity(), ViewTaskActivity.class);
                intent.putExtra("if_new", 0);
                startActivity(intent);
            }
        });
        return view;
    }
}