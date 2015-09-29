package com.nextep.pelmel.activities;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.nextep.pelmel.R;
import com.nextep.pelmel.adapters.NetworkRecycleViewAdapter;
import com.nextep.pelmel.helpers.Utils;
import com.nextep.pelmel.model.support.SnippetChildSupport;
import com.nextep.pelmel.model.support.SnippetContainerSupport;

/**
 * Created by cfondacci on 25/09/15.
 */
public class MyNetworkFragment extends Fragment implements SnippetChildSupport{

    private ListView listview;
    private RecyclerView recyclerView;
    private SnippetContainerSupport snippetContainerSupport;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        final View view = inflater.inflate(R.layout.layout_listview,container,false);
//        listview = (ListView)view.findViewById(R.id.listView);
//        listview.setAdapter(new NetworkAdapter(this.getActivity()));

        final View view = inflater.inflate(R.layout.layout_recyclerview,container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        // Computing columns
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        final Point p = new Point();
        display.getSize(p);
        int i = 1;
        float width = Float.MAX_VALUE;
        float maxWidth = Utils.getDimension(R.dimen.grid_max_cell_width);
        while(width>maxWidth) {
            width = ((float)(p.x - (i+1)) / (float)i);
            i++;
        }
        final GridLayoutManager layoutManager = new GridLayoutManager(this.getActivity(),i);
        final NetworkRecycleViewAdapter adapter = new NetworkRecycleViewAdapter(this.getActivity(),i);
        layoutManager.setSpanSizeLookup(adapter.getSpanSizeLookup());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        snippetContainerSupport.setSnippetChild(this);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.snippetContainerSupport = (SnippetContainerSupport)activity;
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
}
