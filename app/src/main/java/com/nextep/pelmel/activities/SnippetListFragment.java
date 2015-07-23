package com.nextep.pelmel.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.MotionEvent;
import android.view.View;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.adapters.SnippetListAdapter;
import com.nextep.pelmel.adapters.SnippetPlacesListAdapter;
import com.nextep.pelmel.adapters.SnippetSectionedAdapter;
import com.nextep.pelmel.helpers.ContextHolder;
import com.nextep.pelmel.listeners.UserListener;
import com.nextep.pelmel.model.User;
import com.nextep.pelmel.model.support.SnippetContainerSupport;
import com.nextep.pelmel.providers.SnippetInfoProvider;

/**
 * Created by cfondacci on 21/07/15.
 */
public class SnippetListFragment extends ListFragment implements UserListener, View.OnTouchListener {

    private SnippetInfoProvider infoProvider;
    private SnippetSectionedAdapter adapter;
    private SnippetContainerSupport snippetContainerSupport;
    private boolean isOpened = false;

    public void setInfoProvider(SnippetInfoProvider provider) {
        this.infoProvider = provider;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PelMelApplication.getUserService().getCurrentUser(this);
        getListView().setDividerHeight(0);
//        getListView().setClipToPadding(false);
//        Rect frame = new Rect();
//        getListView().getWindowVisibleDisplayFrame(frame);
//        getListView().setPadding(0,frame.height()-110,0,0);
//        getListView().setOnTouchListener(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            snippetContainerSupport = (SnippetContainerSupport)activity;
        } catch(ClassCastException e) {
            throw new IllegalStateException("Parent of SnippetListFragment must be a snippetContainerSupport");
        }
    }

    @Override
    public void userInfoAvailable(User user) {
        adapter = new SnippetSectionedAdapter(this.getActivity());
        adapter.addSection(SnippetSectionedAdapter.SECTION_SNIPPET,new SnippetListAdapter(this.getActivity(), infoProvider));
        adapter.addSection(SnippetSectionedAdapter.SECTION_PLACES,new SnippetPlacesListAdapter(this.getActivity(), ContextHolder.places));
        getListView().setAdapter(adapter);
        setListShown(true);
    }

    @Override
    public void userInfoUnavailable() {

    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(!isOpened) {
            snippetContainerSupport.openSnippet();
            isOpened = true;
            return true;
        } else {
            return false;
        }

    }
}
