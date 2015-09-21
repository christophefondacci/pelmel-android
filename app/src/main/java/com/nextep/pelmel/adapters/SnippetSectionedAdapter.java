package com.nextep.pelmel.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.nextep.pelmel.R;
import com.nextep.pelmel.helpers.ContextHolder;
import com.nextep.pelmel.helpers.Strings;
import com.nextep.pelmel.views.SnippetTabsViewHolder;

/**
 * Created by cfondacci on 22/07/15.
 */
public class SnippetSectionedAdapter extends SectionedAdapter implements View.OnClickListener {
    public static final String SECTION_GALLERY  = "gallery";
    public static final String SECTION_SNIPPET  = "snippet";
    public static final String SECTION_PLACES   = "places";
    public static final String SECTION_ADDRESS  = "address";
    public static final String SECTION_OPENING  = "OPENING";
    public static final String SECTION_HAPPY    = "HAPPY_HOUR";
    public static final String SECTION_THEME    = "THEME";
    public static final String SECTION_EVENTS    = "events";
    public static final String SECTION_DESCRIPTION="description";
    public static final String SECTION_DEALS    ="deals";
    public static final String SECTION_THUMBS   ="thumbs";
    public static final String SECTION_CHECKIN   ="checkin";
    public static final String SECTION_ATTEND   ="attend";
    public static final String SECTION_LOCATION ="location";

    private LayoutInflater layoutInflater;

    private enum Tab { EVENTS, DEALS, PLACES }
    private Tab currentTab;
    private SnippetTabsViewHolder tabsViewHolder;
    private Context context;

    public SnippetSectionedAdapter(Context context) {
        super(context);
        this.context = context;
        layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        currentTab = Tab.PLACES;
    }
    @Override
    protected View getHeaderView(String caption, int index, View convertView, ViewGroup parent) {
        switch(caption) {
            case SECTION_PLACES:
//                if(convertView == null) {
                    convertView = layoutInflater.inflate(R.layout.section_tabs_snippet, parent, false);
//                }
//                if(convertView.getTag() == null) {
                    tabsViewHolder = new SnippetTabsViewHolder();
                    tabsViewHolder.eventsButton = (Button)convertView.findViewById(R.id.eventsButton);
                    tabsViewHolder.dealsButton = (Button)convertView.findViewById(R.id.dealsButton);
                    tabsViewHolder.placesButton = (Button)convertView.findViewById(R.id.placesButton);
                    tabsViewHolder.eventsButton.setTag(Tab.EVENTS);
                    tabsViewHolder.dealsButton.setTag(Tab.DEALS);
                    tabsViewHolder.placesButton.setTag(Tab.PLACES);
                    tabsViewHolder.eventsButton.setOnClickListener(this);
                    tabsViewHolder.dealsButton.setOnClickListener(this);
                    tabsViewHolder.placesButton.setOnClickListener(this);
                    convertView.setTag(tabsViewHolder);
//                } else {
//                    tabsViewHolder = (SnippetTabsViewHolder)convertView.getTag();
//                }
                tabsViewHolder.eventsButton.setText(Strings.getText(R.string.tabEvents));
                tabsViewHolder.eventsButton.setBackgroundResource(R.drawable.bg_tab_enabled);
                tabsViewHolder.dealsButton.setText(Strings.getText(R.string.tabDeals));
                tabsViewHolder.placesButton.setText(Strings.getText(R.string.tabPlaceList));
                refreshTab();
                return convertView;
            case SECTION_ADDRESS:
                return getSectionTitleConvertView(convertView,parent,R.string.section_general_info);
            case SECTION_EVENTS:
                return getSectionTitleConvertView(convertView,parent,R.string.section_events);
            case SECTION_LOCATION:
                return getSectionTitleConvertView(convertView,parent,R.string.section_event_location);
//            case SECTION_CHECKIN:
//                return getSectionTitleConvertView(convertView,parent,R.string.section_current_checkin);
            case SECTION_OPENING:
            case SECTION_HAPPY:
            case SECTION_THEME: {
                if (convertView == null || convertView.getTag() == null) {
                    convertView = layoutInflater.inflate(R.layout.section_subtitle, parent, false);
                    final TextView textView = (TextView) convertView.findViewById(R.id.sectionTitleLabel);
                    convertView.setTag(textView);
                }
                final TextView textView = (TextView)convertView.getTag();
                // Spacer view
                switch(caption) {
                    case SECTION_OPENING:
                        textView.setText(Strings.getText(R.string.section_title_openinghours));
                        break;
                    case SECTION_HAPPY:
                        textView.setText(Strings.getText(R.string.section_title_happyhours));
                        break;
                    case SECTION_THEME:
                        textView.setText(Strings.getText(R.string.section_title_themenights));
                        break;
                }
                return convertView;
            }

        }
        return layoutInflater.inflate(R.layout.list_row_empty,parent,false);
    }


    @Override
    public void onClick(View v) {
        final Tab newTab = (Tab)v.getTag();
        if(newTab != currentTab) {
            currentTab = (Tab) v.getTag();
            switch (currentTab) {
                case EVENTS:
                    replaceSection(SECTION_PLACES, new SnippetEventsListAdapter(context, ContextHolder.events));
                    break;
                case DEALS:
                    replaceSection(SECTION_PLACES, new SnippetEventsListAdapter(context, ContextHolder.happyHours));
                    break;
                case PLACES:
                    replaceSection(SECTION_PLACES, new SnippetPlacesListAdapter(context, ContextHolder.places));
                    break;
            }
            refreshTab();
        }
    }

    private void refreshTab() {
        int eventsRes = R.drawable.bg_tab_disabled;
        int dealsRes = R.drawable.bg_tab_disabled;
        int placesRes = R.drawable.bg_tab_disabled;
        switch(currentTab) {
            case EVENTS:
                eventsRes = R.drawable.bg_tab_enabled;
                break;
            case DEALS:
                dealsRes = R.drawable.bg_tab_enabled;
                break;
            case PLACES:
                placesRes = R.drawable.bg_tab_enabled;
                break;
        }
        if(tabsViewHolder != null) {
            tabsViewHolder.eventsButton.setBackgroundResource(eventsRes);
            tabsViewHolder.dealsButton.setBackgroundResource(dealsRes);
            tabsViewHolder.placesButton.setBackgroundResource(placesRes);
        }
    }
}

