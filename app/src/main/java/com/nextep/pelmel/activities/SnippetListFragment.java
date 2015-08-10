package com.nextep.pelmel.activities;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.R;
import com.nextep.pelmel.adapters.SnippetAddressInfoAdapter;
import com.nextep.pelmel.adapters.SnippetAttendAdapter;
import com.nextep.pelmel.adapters.SnippetCheckinAdapter;
import com.nextep.pelmel.adapters.SnippetDescriptionListAdapter;
import com.nextep.pelmel.adapters.SnippetEventsListAdapter;
import com.nextep.pelmel.adapters.SnippetGalleryAdapter;
import com.nextep.pelmel.adapters.SnippetHoursInfoAdapter;
import com.nextep.pelmel.adapters.SnippetListAdapter;
import com.nextep.pelmel.adapters.SnippetPlacesListAdapter;
import com.nextep.pelmel.adapters.SnippetSectionedAdapter;
import com.nextep.pelmel.adapters.SnippetThumbsListAdapter;
import com.nextep.pelmel.helpers.ContextHolder;
import com.nextep.pelmel.listeners.OverviewListener;
import com.nextep.pelmel.listeners.UserListener;
import com.nextep.pelmel.listview.ExpandableListItem;
import com.nextep.pelmel.model.CalObject;
import com.nextep.pelmel.model.Event;
import com.nextep.pelmel.model.EventType;
import com.nextep.pelmel.model.Place;
import com.nextep.pelmel.model.RecurringEvent;
import com.nextep.pelmel.model.User;
import com.nextep.pelmel.model.support.SnippetChildSupport;
import com.nextep.pelmel.model.support.SnippetContainerSupport;
import com.nextep.pelmel.providers.SnippetInfoProvider;
import com.nextep.pelmel.providers.impl.ContextSnippetInfoProvider;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by cfondacci on 21/07/15.
 */
public class SnippetListFragment extends ListFragment implements UserListener, AdapterView.OnItemClickListener, SnippetChildSupport, OverviewListener, Refreshable {

    private static final String BUNDLE_STATE_ITEM_KEY = "itemKey";
    private static final String BUNDLE_STATE_NULL_OBJ = "null";

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
        if(snippetContainerSupport != null) {
            snippetContainerSupport.setSnippetChild(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ListView lv = (ListView)inflater.inflate(R.layout.list_snippet, container, false);
        return lv;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PelMelApplication.getUserService().getCurrentUser(this);
        getListView().setDividerHeight(0);

        snippetContainerSupport.setSnippetChild(this);
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
    public void userInfoAvailable(final User user) {
        if(infoProvider == null) {
            return;
        }
        if (infoProvider.getItem() != null) {

            new AsyncTask<Void,Void,Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    PelMelApplication.getDataService().getOverviewData(user, infoProvider.getItem(), SnippetListFragment.this);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {

                }
            }.execute();

        }
        updateData();
    }

    @Override
    public void updateData() {
        adapter = new SnippetSectionedAdapter(this.getActivity());
        adapter.addSection(SnippetSectionedAdapter.SECTION_SNIPPET, new SnippetListAdapter(this.getActivity(), this, infoProvider));
        if (infoProvider.getItem() != null) {
            adapter.addSection( SnippetSectionedAdapter.SECTION_GALLERY, new SnippetGalleryAdapter(this.getActivity(), true /*snippetContainerSupport.isSnippetOpened()*/, infoProvider.getItem()));
        }
        if(infoProvider.getItem() == null) {
            adapter.addSection(SnippetSectionedAdapter.SECTION_PLACES, new SnippetPlacesListAdapter(this.getActivity(), ContextHolder.places));
        } else {
            // Thumbs section
            adapter.addSection(SnippetSectionedAdapter.SECTION_THUMBS,new SnippetThumbsListAdapter(this.getActivity(),infoProvider));

            // Checkin section
            if(infoProvider.getItem() instanceof  User) {
                final User user = (User) infoProvider.getItem();
                adapter.addSection(SnippetSectionedAdapter.SECTION_CHECKIN,new SnippetCheckinAdapter(this.getActivity(),user));
                adapter.addSection(SnippetSectionedAdapter.SECTION_ATTEND,new SnippetAttendAdapter(this.getActivity(),user));
            } else if(infoProvider.getItem() instanceof Event) {
                final Event event = (Event)infoProvider.getItem();
                if(event.getPlace()!=null) {
                    adapter.addSection(SnippetSectionedAdapter.SECTION_LOCATION, new SnippetPlacesListAdapter(this.getActivity(), Arrays.asList(event.getPlace())));
                }
            }

            // Address section
            adapter.addSection(SnippetSectionedAdapter.SECTION_ADDRESS, new SnippetAddressInfoAdapter(this.getActivity(), infoProvider));

            // Hours section
            if(infoProvider.getItem() instanceof  Place) {
                // Hashing by hour type, because we are categorizing
                final Map<EventType,List<RecurringEvent>> typedEventsMap = PelMelApplication.getConversionService().buildTypedHoursMap((Place)infoProvider.getItem());

                // Listing in the enum order to make sure we have a stable ordering
                for(EventType eventType : EventType.values()) {
                    final List<RecurringEvent> events = typedEventsMap.get(eventType);
                    // Have we got any entry?
                    if(events != null && !events.isEmpty() && eventType != EventType.THEME) {
                        // If so we add the corresponding hours section
                        adapter.addSection(eventType.name(),new SnippetHoursInfoAdapter(this.getActivity(), events));
                    }
                }
            }

            // Building events list
            final List<Event> events = infoProvider.getEvents();
            if(events != null && !events.isEmpty()) {
                adapter.addSection(SnippetSectionedAdapter.SECTION_EVENTS,new SnippetEventsListAdapter(this.getActivity(),events));
            }

            // Appending description section
            adapter.addSection(SnippetSectionedAdapter.SECTION_DESCRIPTION,new SnippetDescriptionListAdapter(this.getActivity(),infoProvider));

        }
        getListView().setAdapter(adapter);
        getListView().setOnItemClickListener(this);
    }

    @Override
    public void userInfoUnavailable() {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final Object obj = adapter.getItem(position);
        if(obj instanceof CalObject) {

            SnippetInfoProvider provider = PelMelApplication.getUiService().buildInfoProviderFor((CalObject)obj);
            if(provider != null) {
                snippetContainerSupport.showSnippetFor(provider, true, false);
            }
        }
    }

    @Override
    public void onSnippetOpened(boolean snippetOpened) {
        if(adapter != null) {
            SnippetGalleryAdapter galleryAdapter = (SnippetGalleryAdapter)adapter.getSection(SnippetSectionedAdapter.SECTION_GALLERY);
            if(galleryAdapter != null) {
                ExpandableListItem listItem = (ExpandableListItem)galleryAdapter.getItem(0);
//                listItem.getContentView().setTextViewWrap((LinearLayout) view);

                int fromHeight = 0;
                int toHeight = 0;

                if (listItem.isExpanded()) {
                    fromHeight = listItem.getExpandedHeight();
                    toHeight = listItem.getCollapsedHeight();
                } else {
                    fromHeight = listItem.getCollapsedHeight();
                    toHeight = listItem.getExpandedHeight();

                }
                if(listItem.getContentView()!=null) {
//                    toggleAnimation(listItem, fromHeight, toHeight, true);
                }
//                if (snippetOpened) {
//                    ((ExpandingListView) getListView()).expandItem(1);
//                } else {
//                    ((ExpandingListView) getListView()).collapseItem(1);
//                }
            }
        }
    }
    private void toggleAnimation(final ExpandableListItem listItem,
                                 final int fromHeight, final int toHeight, final boolean goToItem) {

//        ResizeAnimation resizeAnimation = new ResizeAnimation(adapter,
//                listItem, 0, fromHeight, 0, toHeight);
//        resizeAnimation.setAnimationListener(new Animation.AnimationListener() {
//
//            @Override
//            public void onAnimationStart(Animation animation) {
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
        AbsListView.LayoutParams p = (AbsListView.LayoutParams) listItem.getContentView().getLayoutParams();
        p.height = (int) toHeight;
//        p.width = (int) width;
        listItem.setCurrentHeight(p.height);
//        ((BaseAdapter)mListAdapter).notifyDataSetChanged();
                listItem.setExpanded(!listItem.isExpanded());
//                listItem.setDrawable(listItem.isOpen() ? R.drawable.up
//                        : R.drawable.down);
                listItem.setCurrentHeight(toHeight);
                adapter.notifyDataSetChanged();
//
////                if (goToItem)
////                    goToItem(position);
//            }
//        });
//
//        listItem.getContentView().startAnimation(resizeAnimation);
    }

    @Override
    public Activity getContext() {
        return this.getActivity();
    }

    @Override
    public void overviewDataAvailable(CalObject object) {
        infoProvider = PelMelApplication.getUiService().buildInfoProviderFor(object);
        updateData();
    }

    @Override
    public View getScrollableView() {
        return getListView();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(infoProvider != null) {
            if (infoProvider.getItem() == null) {
                outState.putString(BUNDLE_STATE_ITEM_KEY, BUNDLE_STATE_NULL_OBJ);
            } else {
                outState.putString(BUNDLE_STATE_ITEM_KEY, infoProvider.getItem().getKey());
            }
        }
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null) {
            final String itemKey = savedInstanceState.getString(BUNDLE_STATE_ITEM_KEY);
            if (itemKey == null || itemKey.equals(BUNDLE_STATE_NULL_OBJ)) {
                infoProvider = new ContextSnippetInfoProvider();
            } else {
                CalObject obj = PelMelApplication.getDataService().getCalObject(itemKey, this);
                infoProvider = PelMelApplication.getUiService().buildInfoProviderFor(obj);
            }
        }
    }
}
