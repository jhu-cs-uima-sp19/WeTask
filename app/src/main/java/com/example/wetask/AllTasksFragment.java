package com.example.wetask;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;


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
    private ListView allList;
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
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_all_tasks, container, false );
        allList = (ListView) view.findViewById(R.id.allTaskItems);
        allList.setAdapter( adapter );
        return view;
    }
}