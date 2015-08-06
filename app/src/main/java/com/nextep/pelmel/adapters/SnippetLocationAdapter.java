package com.nextep.pelmel.adapters;

import android.content.Context;
import android.view.LayoutInflater;

import com.nextep.pelmel.model.Event;

import java.util.Arrays;

/**
 * Created by cfondacci on 06/08/15.
 */
public class SnippetLocationAdapter extends SnippetPlacesListAdapter {

    private LayoutInflater layoutInflater;
    public SnippetLocationAdapter(Context context, Event event) {
        super(context, Arrays.asList(event.getPlace()));
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }
}
