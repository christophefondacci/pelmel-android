package com.nextep.pelmel.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.R;
import com.nextep.pelmel.helpers.Strings;
import com.nextep.pelmel.helpers.Utils;
import com.nextep.pelmel.model.Action;
import com.nextep.pelmel.model.CalObject;
import com.nextep.pelmel.model.CurrentUser;
import com.nextep.pelmel.model.Place;
import com.nextep.pelmel.model.User;
import com.nextep.pelmel.views.HorizontalListView;

import java.util.List;

/**
 * Created by cfondacci on 25/09/15.
 */
public class NetworkRecycleViewAdapter extends SectionedRecyclerAdapter {

    private static final String LOG_TAG = "NETWORK";
    private static final int TABS_VIEW_TYPE=1;
    private static final int INDEX_ACTIONS = 0;
    private static final int INDEX_PENDING_APPROVAL = 1;
    private static final int INDEX_NETWORK = 2;
    private static final int INDEX_PENDING_REQUESTS = 3;
    private static final String SECTION_CHECKINS = "checkins";
    private static final String SECTION_ACTIONS = "actions";
    private static final String SECTION_PENDING_APPROVAL = "approvals";
    private static final String SECTION_NETWORK = "network";
    private static final String SECTION_PENDING_REQUESTS = "requests";


    private Context context;
    private LayoutInflater layoutInflater;
    private CurrentUser currentUser;
    private int columns;

    private enum NetworkTab {
        NETWORK_TAB, CHECKINS_TAB
    }
    private  NetworkTab activeTab = NetworkTab.NETWORK_TAB;

    private class GridThumbViewHolder extends RecyclerView.ViewHolder {
        public RoundedImageView thumbImage;
        public TextView titleLabel;
        public Adapter adapter;
        public int position;
        public GridThumbViewHolder(View parentView) {
            super(parentView);
            this.thumbImage = (RoundedImageView)parentView.findViewById(R.id.thumbImage);
            this.titleLabel = (TextView)parentView.findViewById(R.id.titleLabel);
            this.thumbImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Object o = adapter.getItem(position);
                    if(o instanceof CalObject) {
                        PelMelApplication.getSnippetContainerSupport().showSnippetFor((CalObject) o, true, false);
                    }
                }
            });
        }
    }
    private class NoCheckinViewHolder extends RecyclerView.ViewHolder {
        private TextView noCheckinText;
        private Button noCheckinActionButton;
        public NoCheckinViewHolder(View view) {
            super(view);
            noCheckinText = (TextView)view.findViewById(R.id.noCheckinMsg);
            noCheckinActionButton = (Button)view.findViewById(R.id.noCheckinBtn);
        }
    }
    private class CalObjectViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView titleText;
        public TextView subtitleText;
        public ImageView locationIcon;
        public ImageView likeIcon;
        public TextView locationText;
        public TextView likeText;
        public HorizontalListView usersListView;
        public Adapter adapter;
        public int position;
        public CalObjectViewHolder(View view) {
            super(view);
            imageView = (ImageView)view.findViewById(R.id.image);
            titleText = (TextView)view.findViewById(R.id.dateLabel);
            subtitleText = (TextView)view.findViewById(R.id.titleLabel);
            locationIcon = (ImageView)view.findViewById(R.id.locationIcon);
            locationText = (TextView)view.findViewById(R.id.locationLabel);
            likeIcon = (ImageView)view.findViewById(R.id.countIcon);
            likeText = (TextView)view.findViewById(R.id.countLabel);
            usersListView = (HorizontalListView)view.findViewById(R.id.usersListView);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Place p = (Place)adapter.getItem(position);
                    PelMelApplication.getSnippetContainerSupport().showSnippetFor(p,true,false);
                }
            });
        }


    }
    private class TabsViewHolder extends RecyclerView.ViewHolder{
        Button networkButton;
        Button checkinsButton;
        public TabsViewHolder(View view) {
            super(view);
            this.networkButton = (Button)view.findViewById(R.id.networkButton);
            this.checkinsButton = (Button)view.findViewById(R.id.checkinsButton);
        }
    }


    public NetworkRecycleViewAdapter(Context context, int columns) {
        super(context);
        this.context = context ;
        this.columns = columns;
        this.layoutInflater = LayoutInflater.from(context);
        this.currentUser = PelMelApplication.getUserService().getLoggedUser();
        addNetworkSections();

    }

    private void addNetworkSections() {
        addSection(SECTION_ACTIONS,new NetworkActionsGridAdapter(context));
        addSection(SECTION_PENDING_APPROVAL,new CALObjectGridAdapter(context,currentUser.getNetworkPendingApprovals(),columns));
        addSection(SECTION_NETWORK,new CALObjectGridAdapter(context,currentUser.getNetworkUsers(),columns));
        addSection(SECTION_PENDING_REQUESTS, new CALObjectGridAdapter(context, currentUser.getNetworkPendingRequests(), columns));
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0) {
            return TABS_VIEW_TYPE;
        } else {
//            if(activeTab == NetworkTab.NETWORK_TAB) {
                return super.getItemViewType(position);
//            } else {
//                return 999+super.getItemViewType(position);
//            }
        }

    }

    private boolean hasNetworkCheckins() {
        final CurrentUser user = PelMelApplication.getUserService().getLoggedUser();
        boolean hasCheckin = false;
        for(User u : user.getNetworkUsers()) {
            if(PelMelApplication.getUserService().getCheckedInPlace(u)!=null) {
                hasCheckin = true;
                break;
            }
        }
        return hasCheckin;
    }
    private boolean hasNetwork() {
        return !PelMelApplication.getUserService().getLoggedUser().getNetworkUsers().isEmpty();
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if(viewType == TABS_VIEW_TYPE) {
            final View view = layoutInflater.inflate(R.layout.section_tabs_network,viewGroup,false);
            final TabsViewHolder viewHolder = new TabsViewHolder(view);
            viewHolder.checkinsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(activeTab!=NetworkTab.CHECKINS_TAB) {
                        activeTab = NetworkTab.CHECKINS_TAB;
                        removeSection(SECTION_ACTIONS);
                        removeSection(SECTION_PENDING_APPROVAL);
                        removeSection(SECTION_NETWORK);
                        removeSection(SECTION_PENDING_REQUESTS);
                        addSection(SECTION_CHECKINS, new NetworkCheckinsAdapter(context));
                        viewHolder.checkinsButton.setBackgroundResource(R.drawable.bg_tab_enabled);
                        viewHolder.networkButton.setBackgroundResource(R.drawable.bg_tab_disabled);
                    }
                }
            });
            viewHolder.networkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (activeTab != NetworkTab.NETWORK_TAB) {
                        activeTab = NetworkTab.NETWORK_TAB;
                        removeSection(SECTION_CHECKINS);
                        addNetworkSections();
                        viewHolder.checkinsButton.setBackgroundResource(R.drawable.bg_tab_disabled);
                        viewHolder.networkButton.setBackgroundResource(R.drawable.bg_tab_enabled);
                    }
                }
            });
            return viewHolder;
        } else if(viewType == TYPE_SECTION_HEADER) {
            return super.onCreateViewHolder(viewGroup,viewType);
        } else {
            if(activeTab == NetworkTab.NETWORK_TAB) {
                final View view = layoutInflater.inflate(R.layout.grid_thumb, viewGroup, false);
                return new GridThumbViewHolder(view);
            } else {
                int noCheckinsViewType = getViewType(getSection(SECTION_CHECKINS), NetworkCheckinsAdapter.VIEW_TYPE_NO_CHECKIN);
                Log.d(LOG_TAG,"ViewType: " + viewType + " VERSUS " + noCheckinsViewType);
                if(viewType ==  noCheckinsViewType){
                    final View view = layoutInflater.inflate(R.layout.list_row_network_checkins,viewGroup,false);
                    return new NoCheckinViewHolder(view);
                } else {
                    final View view = layoutInflater.inflate(R.layout.list_row_cal_object, viewGroup, false);
                    return new CalObjectViewHolder(view);
                }
            }
        }
    }


    @Override
    protected void bindHeaderViewHolder(RecyclerView.ViewHolder viewHolder, int sectionIndex) {
        Log.d(LOG_TAG, viewHolder.getClass().toString());
        switch(sectionIndex) {
            case INDEX_PENDING_APPROVAL:
                ((TextViewHolder)viewHolder).textView.setText(Strings.getText(R.string.network_section_pendingApproval));
                break;
            case INDEX_NETWORK:
                ((TextViewHolder)viewHolder).textView.setText(Strings.getText(R.string.network_section_network));
                break;
            case INDEX_PENDING_REQUESTS:
                ((TextViewHolder)viewHolder).textView.setText(Strings.getText(R.string.network_section_pendingRequest));
                break;
        }
        if(getSections().get(sectionIndex).adapter.getCount()==0 && sectionIndex>0) {
            viewHolder.itemView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void bindViewHolder(RecyclerView.ViewHolder vh, Adapter adapter, int sectionIndex, int adapterPosition) {

        if(activeTab == NetworkTab.NETWORK_TAB) {
            final GridThumbViewHolder viewHolder = (GridThumbViewHolder) vh;
            viewHolder.adapter = adapter;
            viewHolder.position = adapterPosition;
            if (sectionIndex == INDEX_ACTIONS) {
                if (adapterPosition == 0) {
                    viewHolder.thumbImage.setImageResource(R.drawable.btn_network_chat);
                    viewHolder.titleLabel.setText(Strings.getText(R.string.network_action_groupChat));
                    viewHolder.thumbImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PelMelApplication.getActionManager().executeAction(Action.GROUP_CHAT,null);
                        }
                    });
                } else if (adapterPosition == 1) {
                    viewHolder.thumbImage.setImageResource(R.drawable.btn_network_add);
                    viewHolder.titleLabel.setText(Strings.getText(R.string.network_action_add));
                    viewHolder.thumbImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PelMelApplication.getActionManager().executeAction(Action.NETWORK_PICK,null);
                        }
                    });
                }
                viewHolder.thumbImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                viewHolder.thumbImage.setAdjustViewBounds(true);
            } else {
                final CalObject obj = (CalObject) adapter.getItem(adapterPosition);
                if (obj != null) {
                    if (obj.getThumb() != null) {
                        PelMelApplication.getImageService().displayImage(obj.getThumb(), false, viewHolder.thumbImage);
                    } else {
                        PelMelApplication.getImageService().cancelDisplay(viewHolder.thumbImage);
                        final Bitmap b = PelMelApplication.getUiService().getNoPhotoFor(obj, false);
                        viewHolder.thumbImage.setImageBitmap(b);
                    }
                    viewHolder.thumbImage.setBorderWidth(0.0f);
                    viewHolder.thumbImage.setBorderColor(Utils.getColor(R.color.transparent));
                    if (obj instanceof User) {
                        if (((User) obj).isOnline()) {
                            viewHolder.thumbImage.setBorderWidth(1.0f);
                            viewHolder.thumbImage.setBorderColor(Utils.getColor(R.color.online));
                        }
                    }
                    viewHolder.titleLabel.setText(obj.getName());
                } else {
                    viewHolder.thumbImage.setImageBitmap(null);
                    viewHolder.thumbImage.setBorderWidth(0.0f);
                    viewHolder.titleLabel.setText(null);
                }
            }
        } else if(activeTab == NetworkTab.CHECKINS_TAB) {
            final NetworkCheckinsAdapter a = (NetworkCheckinsAdapter) adapter;
            final CurrentUser user = PelMelApplication.getUserService().getLoggedUser();
            if(vh instanceof NoCheckinViewHolder) {
                final NoCheckinViewHolder viewHolder = (NoCheckinViewHolder)vh;
                Strings.setFontFamily(viewHolder.noCheckinText);
                Strings.setFontFamily(viewHolder.noCheckinActionButton);
                if(!hasNetwork()) {
                    viewHolder.noCheckinText.setText(Strings.getText(R.string.network_checkins_noNetworkMessage));
                    viewHolder.noCheckinActionButton.setText(Strings.getText(R.string.network_checkins_addToNetwork));
                    viewHolder.noCheckinActionButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ico_network_button,0,0,0);
                    viewHolder.noCheckinActionButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PelMelApplication.getActionManager().executeAction(Action.NETWORK_PICK,null);
                        }
                    });
                } else if(! hasNetworkCheckins()) {
                    viewHolder.noCheckinText.setText(Strings.getText(R.string.network_checkins_noCheckinMessage));
                    viewHolder.noCheckinActionButton.setText(Strings.getText(R.string.network_checkins_startChat));
                    viewHolder.noCheckinActionButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ico_chat, 0, 0, 0);
                }
            } else {
                // Retyping our known objects
                final CalObjectViewHolder viewHolder = (CalObjectViewHolder) vh;


                // Getting our place object
                final Place p = (Place) a.getItem(adapterPosition);

                // Photo loading
                if (p.getThumb() != null) {
                    PelMelApplication.getImageService().displayImage(p.getThumb(), false, viewHolder.imageView);
                } else {
                    final Bitmap b = PelMelApplication.getUiService().getNoPhotoFor(p, false, false);
                    viewHolder.imageView.setImageBitmap(b);
                }

                // Place title and subtitle
                viewHolder.titleText.setText(p.getName());
                double dist = PelMelApplication.getConversionService().getDistanceTo(p);
                String distStr = p.getCityName();
                if (dist > 0) {
                    distStr = PelMelApplication.getConversionService().getDistanceStringForMiles(dist);
                }
                distStr += " - " + Strings.getText(PelMelApplication.getUiService().getLabelForPlaceType(p.getType()));
                viewHolder.subtitleText.setText(distStr);

                // Hiding location / like icon & label
                viewHolder.locationText.setVisibility(View.INVISIBLE);
                viewHolder.locationIcon.setVisibility(View.INVISIBLE);
                viewHolder.likeText.setVisibility(View.INVISIBLE);
                viewHolder.likeIcon.setVisibility(View.INVISIBLE);

                // Users list view
                final List<User> users = a.getUsers(p);
                final CALObjectThumbAdapter calObjectThumbAdapter = new CALObjectThumbAdapter(context, users);
                viewHolder.usersListView.setAdapter(calObjectThumbAdapter);
                viewHolder.usersListView.setOnItemClickListener(calObjectThumbAdapter);
                viewHolder.adapter = a;
                viewHolder.position = adapterPosition;
            }
        }

    }

    @Override
    public int getItemCount() {
        final int count = super.getItemCount();
        if(activeTab == NetworkTab.CHECKINS_TAB) {
            return Math.max(count,2);
        }
        return count;
    }

    public GridLayoutManager.SpanSizeLookup getSpanSizeLookup() {
         return new GridLayoutManager.SpanSizeLookup() {
             @Override
             public int getSpanSize(int i) {

                 if(activeTab == NetworkTab.CHECKINS_TAB) {
                     return columns;
                 } else {
                     int sectionIndex = 0;
                     int position = i;
                     for (Section section : getSections()) {
                         if (position == 0) {
                             return columns;
                         }
                         int size = section.adapter.getCount() + 1;

                         if (position < size) {
                             return 1;
                         } else {
                             position -= size;
                             sectionIndex++;
                         }
                     }
                     return 1;
                 }
             }
        };
    }


}
