package com.nextep.pelmel.model.support;

import android.content.Intent;

/**
 * Created by cfondacci on 22/09/15.
 */
public interface SnippetChildSupportWithActivityResult extends SnippetChildSupport {

    /**
     * Delegate method for the activity
     * @param requestCode
     * @param resultCode
     * @param data
     */
    void onActivityResult(int requestCode, int resultCode, Intent data);
}
