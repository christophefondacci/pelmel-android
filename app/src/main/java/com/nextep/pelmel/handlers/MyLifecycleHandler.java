package com.nextep.pelmel.handlers;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

public class MyLifecycleHandler implements Application.ActivityLifecycleCallbacks {
    // I use two separate variables here. You can, of course, just use one and
    // increment/decrement it instead of using two and incrementing both.
    private static int resumed;
    private static int stopped;
    
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    public void onActivityDestroyed(Activity activity) {
    }
    
    public void onActivityResumed(Activity activity) {
        ++resumed;
    }
    
    public void onActivityPaused(Activity activity) {
    }

    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    public void onActivityStarted(Activity activity) {
    }

    public void onActivityStopped(Activity activity) {
        ++stopped;
        android.util.Log.w("test", "application is being backgrounded: " + (resumed == stopped));
    }


    public static boolean isApplicationInForeground() {
        return resumed > stopped;
    }
}