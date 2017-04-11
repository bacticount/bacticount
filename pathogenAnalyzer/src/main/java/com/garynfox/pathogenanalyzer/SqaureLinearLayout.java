package com.garynfox.pathogenanalyzer;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by Sarah on 4/1/17.
 */

public class SqaureLinearLayout extends LinearLayout {

    public SqaureLinearLayout(Context context) {
        super(context);
    }

    public SqaureLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SqaureLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
