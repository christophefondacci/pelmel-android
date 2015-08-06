package com.nextep.pelmel.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by cfondacci on 05/08/15.
 */
public class HorizontalViewPager extends ViewPager {

    private float downXpos,downYpos;
    private boolean touchcaptured;
    public HorizontalViewPager(Context context) {
        super(context);
    }

    public HorizontalViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
//        requestDisallowInterceptTouchEvent(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        getParent().requestDisallowInterceptTouchEvent(true);
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downXpos = event.getX();
                downYpos = event.getY();
                touchcaptured = false;
                break;
            case MotionEvent.ACTION_UP:
                requestDisallowInterceptTouchEvent(false);
                break;
            case MotionEvent.ACTION_MOVE:
                float xdisplacement = Math.abs(event.getX() - downXpos);
                float ydisplacement = Math.abs(event.getY() - downYpos);
                if( !touchcaptured && xdisplacement > ydisplacement && xdisplacement > 2) {
                    requestDisallowInterceptTouchEvent(true);
                    touchcaptured = true;
                }
                break;
        }
        super.onTouchEvent(event);
        return true;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        //Call super first because it does some hidden motion event handling
        boolean result = super.onInterceptTouchEvent(event);

        float xdisplacement = Math.abs(event.getX() - downXpos);
        float ydisplacement = Math.abs(event.getY() - downYpos);
        //Now see if we are scrolling vertically with the custom gesture detector
        if (ydisplacement>xdisplacement) {
            return result;
        }
        //If not scrolling vertically (more y than x), don't hijack the event.
        else {
            return false;
        }
    }


}
