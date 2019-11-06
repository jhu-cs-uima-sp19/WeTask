package com.example.wetask;

import android.content.Context;

import com.example.wetask.R;
import com.example.wetask.AllTasksFragment;
import com.example.wetask.ArchiveFragment;
import com.example.wetask.MyTasksFragment;
import com.example.wetask.TaskItemAdapter;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3};
    private final Context mContext;
    private ArrayList<TaskItemAdapter> passed_adapters;

    public SectionsPagerAdapter(Context context,  FragmentManager fm, ArrayList<TaskItemAdapter> adap) {
        super( fm );
        mContext = context;
        passed_adapters = adap;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        //return AllTasksFragment.newInstance( position + 1 );
        switch(position) {
            case 0:
                return MyTasksFragment.newInstance(passed_adapters.get(0));
            case 1:
                return AllTasksFragment.newInstance(passed_adapters.get(1));
            case 2:
                return ArchiveFragment.newInstance(passed_adapters.get(2));
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources( ).getString( TAB_TITLES[position] );
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }
}