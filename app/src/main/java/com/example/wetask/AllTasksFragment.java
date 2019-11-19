package com.example.wetask;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * { AllTasksFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AllTasksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AllTasksFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

//    private PageViewModel pageViewModel;
    ListView allList;
    private static TaskItemAdapter adapter;

    public static AllTasksFragment newInstance(TaskItemAdapter aa) {
        AllTasksFragment fragment = new AllTasksFragment( );
        Bundle bundle = new Bundle( );
        //bundle.putInt( ARG_SECTION_NUMBER, index );
        fragment.setArguments( bundle );
        adapter = aa;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
//        pageViewModel = ViewModelProviders.of( this ).get( PageViewModel.class );
//        int index = 1;
//        if (getArguments( ) != null) {
//            index = getArguments( ).getInt( ARG_SECTION_NUMBER );
//        }
//        pageViewModel.setIndex( index );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_tab, container, false );
        allList = (ListView) view.findViewById(R.id.taskList);
        allList.setAdapter( adapter );
        allList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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