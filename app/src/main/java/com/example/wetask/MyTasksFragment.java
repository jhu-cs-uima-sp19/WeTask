package com.example.wetask;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.material.snackbar.Snackbar;

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
    public static MyTasksFragment newInstance(TaskItemAdapter aa) {
        MyTasksFragment fragment = new MyTasksFragment( );
        Bundle args = new Bundle( );
        args.putString( ARG_PARAM1, "");
        fragment.setArguments( args );
        adapter = aa;
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
                //Snackbar.make(view, "Selected #" + id, Snackbar.LENGTH_SHORT)
                //        .setAction("Action", null).show();
                Intent intent = new Intent(getActivity(), ViewTaskActivity.class);
                //put extra with task id (so know to show details)
                intent.putExtra("TASK_ID", id); //this isn't quite right yet
                startActivity(intent);
            }
        });
        return view;
    }
}
