package com.nextep.pelmel.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by cfondacci on 21/09/15.
 */
public class GridThumb extends RelativeLayout {

    public GridThumb(Context context, AttributeSet attrs) {
        super(context,attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);


        if (getLayoutParams() != null && w != h) {
            getLayoutParams().height = w;
            setLayoutParams(getLayoutParams());
        }
    }

}
