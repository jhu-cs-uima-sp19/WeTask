package com.example.wetask.main;

import android.content.Context;

import com.example.wetask.R;
import com.example.wetask.AllTasksFragment;
import com.example.wetask.ArchiveFragment;
import com.example.wetask.MyTasksFragment;

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

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super( fm );
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        //return AllTasksFragment.newInstance( position + 1 );

        switch(position) {
            case 0:
                return MyTasksFragment.newInstance("dummy1","dummy2");
            case 1:
                return AllTasksFragment.newInstance(2);
            case 2:
                return ArchiveFragment.newInstance("dummy3", "dummy4");
            default:
                return AllTasksFragment.newInstance(2);
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources( ).getString( TAB_TITLES[position] );
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 3;
    }
}