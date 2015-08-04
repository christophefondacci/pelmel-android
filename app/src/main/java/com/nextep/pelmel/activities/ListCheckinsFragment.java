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
import com.nextep.pelmel.adapters.SnippetPlacesListAdapter;
import com.nextep.pelmel.helpers.ContextHolder;
import com.nextep.pelmel.model.Action;
import com.nextep.pelmel.model.Place;
import com.nextep.pelmel.services.ActionManager;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by cfondacci on 03/08/15.
 */
public class ListCheckinsFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

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
        titleView.setText(R.string.checkins_list_title);
        listView.setOnItemClickListener(this);

        // Getting list of available checkins
        List<Place> places = ContextHolder.places;
        Collections.sort(places, new Comparator<Place>() {
            @Override
            public int compare(Place lhs, Place rhs) {
                return (int)(lhs.getDistance() - rhs.getDistance());
            }
        });

        // Setting list adapter
        listView.setAdapter(new SnippetPlacesListAdapter(getActivity(),places));
        return view;
    }

    @Override
    public void onClick(View v) {
        PelMelApplication.getSnippetContainerSupport().dismissDialog();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final Place place = (Place)listView.getAdapter().getItem(position);
        if(place != null) {
            PelMelApplication.getActionManager().executeAction(Action.CHECKIN, place, new ActionManager.ActionCallback() {
                @Override
                public void actionCompleted(boolean isSucess, Object result) {
                    PelMelApplication.getSnippetContainerSupport().showSnippetFor(place,false,false);
                    PelMelApplication.getSnippetContainerSupport().dismissDialog();
                    PelMelApplication.getUiService().showInfoMessage(ListCheckinsFragment.this.getActivity(), R.string.alert_checkin_success_title, R.string.alert_checkin_success);

                }
            });
        }
    }
}
