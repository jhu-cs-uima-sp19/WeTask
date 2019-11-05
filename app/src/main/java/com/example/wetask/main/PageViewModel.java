package com.example.wetask.main;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class PageViewModel extends ViewModel {

    private MutableLiveData<Integer> mIndex = new MutableLiveData<>( );
    private LiveData<String> mText = Transformations.map( mIndex, new Function<Integer, String>( ) {
        @Override
        public String apply(Integer input) {
            //return "Hello world from section: " + input;
            switch (input) {
                case 1:
                    return "My Tasks Placeholder";
                case 2:
                    return "All Tasks Placeholder";
                case 3:
                    return "Archive Placeholder";
                default:
                    return "something is broken";
            }
        }
    } );

    public void setIndex(int index) {
        mIndex.setValue( index );
    }

    public LiveData<String> getText() {
        return mText;
    }
}