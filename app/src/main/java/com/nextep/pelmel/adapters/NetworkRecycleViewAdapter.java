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
import com.nextep.pelmel.model.CalObject;
import com.nextep.pelmel.model.CurrentUser;
import com.nextep.pelmel.model.User;

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
    private static final String SECTION_ACTIONS = "actions";
    private static final String SECTION_PENDING_APPROVAL = "approvals";
    private static final String SECTION_NETWORK = "network";
    private static final String SECTION_PENDING_REQUESTS = "requests";


    private Context context;
    private LayoutInflater layoutInflater;
    private CurrentUser currentUser;
    private int columns;

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

        addSection(SECTION_ACTIONS,new NetworkActionsGridAdapter(context));
        addSection(SECTION_PENDING_APPROVAL,new CALObjectGridAdapter(context,currentUser.getNetworkPendingApprovals(),columns));
        addSection(SECTION_NETWORK,new CALObjectGridAdapter(context,currentUser.getNetworkUsers(),columns));
        addSection(SECTION_PENDING_REQUESTS,new CALObjectGridAdapter(context,currentUser.getNetworkPendingRequests(),columns));
    }



    @Override
    public int getItemViewType(int position) {
        if(position == 0) {
            return TABS_VIEW_TYPE;
        } else {
            return super.getItemViewType(position);
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if(viewType == TABS_VIEW_TYPE) {
            final View view = layoutInflater.inflate(R.layout.section_tabs_network,viewGroup,false);
            final TabsViewHolder viewHolder = new TabsViewHolder(view);
            return viewHolder;
        } else if(viewType == TYPE_SECTION_HEADER) {
            return super.onCreateViewHolder(viewGroup,viewType);
        } else {
            final View view = layoutInflater.inflate(R.layout.grid_thumb,viewGroup,false);
            return new GridThumbViewHolder(view);
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
        if(getSections().get(sectionIndex).adapter.getCount()==0) {
            viewHolder.itemView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void bindViewHolder(RecyclerView.ViewHolder vh, Adapter adapter, int sectionIndex, int adapterPosition) {

        final GridThumbViewHolder viewHolder = (GridThumbViewHolder)vh;
        viewHolder.adapter = adapter;
        viewHolder.position = adapterPosition;
        if(sectionIndex == INDEX_ACTIONS) {
            if(adapterPosition == 0) {
                viewHolder.thumbImage.setImageResource(R.drawable.btn_network_chat);
                viewHolder.titleLabel.setText(Strings.getText(R.string.network_action_groupChat));
            } else if(adapterPosition == 1) {
                viewHolder.thumbImage.setImageResource(R.drawable.btn_network_add);
                viewHolder.titleLabel.setText(Strings.getText(R.string.network_action_add));
            }
                viewHolder.thumbImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                viewHolder.thumbImage.setAdjustViewBounds(true);
        } else {
            final CalObject obj = (CalObject)adapter.getItem(adapterPosition);
            if(obj != null) {
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

    }


    public GridLayoutManager.SpanSizeLookup getSpanSizeLookup() {
         return new GridLayoutManager.SpanSizeLookup() {
             @Override
             public int getSpanSize(int i) {
                 int sectionIndex = 0;
                 int position = i;
                 for (Section section : getSections()) {
                     if (position == 0) {
                         return columns;
                     }
                     int size = section.adapter.getCount() + 1;

//                     if(position == size-1) {
//                         return 3 - (position % 3);
//                     } else
                     if (position < size) {
                         return 1;
                     } else {
                         position -= size;
                         sectionIndex++;
                     }
                 }
                 return 1;
             }
        };
    }


}
