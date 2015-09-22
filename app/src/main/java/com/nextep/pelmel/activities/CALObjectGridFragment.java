package com.nextep.pelmel.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.R;
import com.nextep.pelmel.adapters.CALObjectGridAdapter;
import com.nextep.pelmel.model.CalObject;
import com.nextep.pelmel.model.support.SnippetChildSupport;
import com.nextep.pelmel.model.support.SnippetContainerSupport;

import java.util.Collections;
import java.util.List;

/**
 * Created by cfondacci on 21/09/15.
 */
public class CALObjectGridFragment extends Fragment implements SnippetChildSupport, AdapterView.OnItemClickListener {

    private GridView gridView;
    private List<CalObject> calObjects = Collections.emptyList();
    private SnippetContainerSupport snippetContainerSupport;

    public void setCalObjects(List<CalObject> calObjects) {
        this.calObjects = calObjects;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_gridview,container,false);
        gridView = (GridView)view.findViewById(R.id.gridView);

        gridView.setAdapter(new CALObjectGridAdapter(this.getActivity(),calObjects));
        gridView.setOnItemClickListener(this);
        snippetContainerSupport.setSnippetChild(this);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof SnippetContainerSupport) {
            snippetContainerSupport = (SnippetContainerSupport) activity;
        }
    }

    @Override
    public void onSnippetOpened(boolean snippetOpened) {

    }

    @Override
    public View getScrollableView() {
        return null;
    }

    @Override
    public void updateData() {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final CalObject obj = calObjects.get(position);
        PelMelApplication.getSnippetContainerSupport().showSnippetFor(obj,true,false);
    }
}
