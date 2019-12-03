package com.example.wetask;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridLayout;
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

    private String groupId = "g100"; // need to figure out how to get group id
    private ArrayList<TaskItem> tasks;

    public TabFragment() {
        // Required empty public constructor
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
//        ListView list = (ListView) view.findViewById(R.id.taskList);

        //RecyclerView list = view.findViewById(R.id.taskList);
        tasks = new ArrayList<TaskItem>();
        //TaskItemAdapter adapter = new TaskItemAdapter(getActivity(), R.layout.task_item_layout, tasks);
        TaskItemAdapter adapter = new TaskItemAdapter(tasks);

        int tab = 3;
        if (getArguments( ) != null) {
            tab = getArguments( ).getInt(TAB);
        }

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

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.taskList);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        //LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        int numColumns = 2;
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), numColumns);
        recyclerView.setLayoutManager(layoutManager);
        // specify an adapter (see also next example)
        recyclerView.setAdapter(adapter);
        return view;
    }
}