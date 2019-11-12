package com.example.wetask;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * { MyTasksFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyTasksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyTasksFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ListView myList;

    private static TaskItemAdapter adapter;

    //private OnFragmentInteractionListener mListener;

    public MyTasksFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MyTasksFragment newInstance() {
        MyTasksFragment fragment = new MyTasksFragment( );
        Bundle args = new Bundle( );
        args.putString( ARG_PARAM1, "");
        fragment.setArguments( args );
        adapter = MainActivity.myTaskAdapter;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        if (getArguments( ) != null) {
            mParam1 = getArguments( ).getString( ARG_PARAM1 );
            //mParam2 = getArguments( ).getString( ARG_PARAM2 );
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate( R.layout.fragment_my_tasks, container, false );
        myList = (ListView) view.findViewById(R.id.myTaskItems);
        myList.setAdapter( adapter );
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //get task at position from database, put data in shared prefs from there

                TaskItem selected = adapter.getTaskAtPos(position);

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
