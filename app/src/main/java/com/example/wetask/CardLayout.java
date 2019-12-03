package com.example.wetask;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class CardLayout extends LinearLayout {

    public CardLayout(Context context) {
        super(context);
    }

    public CardLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CardLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int width, int height) {
        // note we are applying the width value as the height
        super.onMeasure(width, width);
    }

}