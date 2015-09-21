package com.nextep.pelmel.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.R;
import com.nextep.pelmel.adapters.DealsListAdapter;
import com.nextep.pelmel.helpers.ContextHolder;
import com.nextep.pelmel.model.Deal;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by cfondacci on 03/08/15.
 */
public class ListDealsFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private ListView listView;
    private TextView titleView;
    private TextView leftButtonView;
    private TextView rightButtonView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_list_objects,container,false);

        // Getting controls
        listView = (ListView)view.findViewById(R.id.dialogListView);
        leftButtonView = (TextView)view.findViewById(R.id.dialogLeftButton);
        rightButtonView = (TextView)view.findViewById(R.id.dialogRightButton);
        titleView = (TextView)view.findViewById(R.id.dialogTitle);

        // Configuring contents
        rightButtonView.setText(null);
        leftButtonView.setText(R.string.cancel);
        leftButtonView.setOnClickListener(this);
        titleView.setText(R.string.deal_list_title);
        listView.setOnItemClickListener(this);

        // Getting list of available checkins
        List<Deal> deals = ContextHolder.deals;
        Collections.sort(deals, new Comparator<Deal>() {
            @Override
            public int compare(Deal lhs, Deal rhs) {
                final double dist1 = PelMelApplication.getConversionService().getDistanceTo(lhs.getRelatedObject());
                final double dist2 = PelMelApplication.getConversionService().getDistanceTo(rhs.getRelatedObject());
                return (int)(dist1*1000 - dist2*1000);
            }
        });

        // Setting list adapter
        listView.setAdapter(new DealsListAdapter(getActivity(),deals));
        return view;
    }

    @Override
    public void onClick(View v) {
        PelMelApplication.getSnippetContainerSupport().dismissDialog();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final Deal deal = (Deal)listView.getAdapter().getItem(position);
        if(deal != null) {
            PelMelApplication.getSnippetContainerSupport().showSnippetFor(deal.getRelatedObject(),true,false);
            PelMelApplication.getSnippetContainerSupport().dismissDialog();
        }
    }
}
