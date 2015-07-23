package com.nextep.pelmel.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.nextep.pelmel.R;
import com.nextep.pelmel.helpers.Strings;
import com.nextep.pelmel.views.SnippetTabsViewHolder;

/**
 * Created by cfondacci on 22/07/15.
 */
public class SnippetSectionedAdapter extends SectionedAdapter {
    public static final String SECTION_SNIPPET  = "snippet";
    public static final String SECTION_PLACES   = "places";
    private LayoutInflater layoutInflater;

    public SnippetSectionedAdapter(Context context) {
        layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    protected View getHeaderView(String caption, int index, View convertView, ViewGroup parent) {
        switch(caption) {
            case SECTION_PLACES:
                SnippetTabsViewHolder viewHolder;
//                if(convertView == null) {
                    convertView = layoutInflater.inflate(R.layout.section_tabs_snippet, parent, false);
//                }
//                if(convertView.getTag() == null) {
                    viewHolder = new SnippetTabsViewHolder();
                    viewHolder.eventsButton = (Button)convertView.findViewById(R.id.eventsButton);
                    viewHolder.dealsButton = (Button)convertView.findViewById(R.id.dealsButton);
                    viewHolder.placesButton = (Button)convertView.findViewById(R.id.placesButton);
                    convertView.setTag(viewHolder);
//                } else {
//                    viewHolder = (SnippetTabsViewHolder)convertView.getTag();
//                }
                viewHolder.eventsButton.setText(Strings.getText(R.string.tabEvents));
                viewHolder.eventsButton.setBackgroundResource(R.drawable.bg_tab_enabled);
                viewHolder.dealsButton.setText(Strings.getText(R.string.tabDeals));
                viewHolder.placesButton.setText(Strings.getText(R.string.tabPlaceList));
                return convertView;
        }
        return layoutInflater.inflate(R.layout.list_row_empty,parent,false);
    }
}

