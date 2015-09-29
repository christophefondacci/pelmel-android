package com.nextep.pelmel.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.R;
import com.nextep.pelmel.helpers.Strings;
import com.nextep.pelmel.model.CurrentUser;

/**
 * Created by cfondacci on 25/09/15.
 */
public class NetworkAdapter extends BaseAdapter {

    private static final int INDEX_TABS = 0;
    private static final int INDEX_ACTIONS = 1;
    private static final int INDEX_PENDING_TITLE = 2;
    private static final int INDEX_PENDING_GRID = 3;
    private static final int INDEX_NETWORK_TITLE = 4;
    private static final int INDEX_NETWORK_GRID = 5;


    private Context context;
    private LayoutInflater layoutInflater;
    private CurrentUser currentUser;

    public NetworkAdapter(Context context) {
        this.context = context ;
        this.layoutInflater = LayoutInflater.from(context);
        this.currentUser = PelMelApplication.getUserService().getLoggedUser();
    }


    @Override
    public int getCount() {
        return currentUser.getNetworkPendingApprovals().isEmpty() ? 4 : 6;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == INDEX_TABS || position == INDEX_ACTIONS ) {
            return position;
        } else {
            if(position == INDEX_NETWORK_TITLE || position == INDEX_PENDING_TITLE) {
                return 2;
            } else {
                return 3;
            }
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(position == INDEX_TABS) {
            TabsViewHolder viewHolder = (TabsViewHolder)(convertView == null ? null : convertView.getTag());
            if (viewHolder == null) {
                convertView = layoutInflater.inflate(R.layout.section_tabs_network,parent,false);
                viewHolder = new TabsViewHolder();
                viewHolder.networkButton = (Button)convertView.findViewById(R.id.networkButton);
                viewHolder.checkinsButton = (Button)convertView.findViewById(R.id.checkinsButton);
                convertView.setTag(viewHolder);
            }
        } else if(position == INDEX_ACTIONS) {
            convertView = getGridView(convertView,parent);
            final GridViewHolder viewHolder = (GridViewHolder)convertView.getTag();
            viewHolder.gridView.setAdapter(new NetworkActionsGridAdapter(context));
        } else if(position == INDEX_PENDING_TITLE && !currentUser.getNetworkPendingApprovals().isEmpty()) {
            convertView = getSectionsTitleView(convertView,parent);
            final SectionViewHolder holder = (SectionViewHolder)convertView.getTag();
            holder.sectionTitle.setText(Strings.getText(R.string.network_section_pendingApproval));
        } else if(position == INDEX_PENDING_GRID && !currentUser.getNetworkPendingApprovals().isEmpty()) {
            convertView = getGridView(convertView,parent);
            final GridViewHolder viewHolder = (GridViewHolder)convertView.getTag();
            viewHolder.gridView.setAdapter(new CALObjectGridAdapter(context,currentUser.getNetworkPendingApprovals()));
        } else if((position == INDEX_NETWORK_TITLE && !currentUser.getNetworkPendingApprovals().isEmpty()) || (position == INDEX_PENDING_TITLE && currentUser.getNetworkPendingApprovals().isEmpty())) {
            convertView = getSectionsTitleView(convertView,parent);
            final SectionViewHolder holder = (SectionViewHolder)convertView.getTag();
            holder.sectionTitle.setText(Strings.getText(R.string.network_section_network));
        } else if((position == INDEX_NETWORK_GRID && !currentUser.getNetworkPendingApprovals().isEmpty()) ||(position==INDEX_PENDING_GRID && currentUser.getNetworkPendingApprovals().isEmpty())) {
            convertView = getGridView(convertView,parent);
            final GridViewHolder viewHolder = (GridViewHolder)convertView.getTag();
            viewHolder.gridView.setAdapter(new CALObjectGridAdapter(context,currentUser.getNetworkUsers()));
        }
        return convertView;
    }

    private View getSectionsTitleView(View convertView, ViewGroup parent) {
        SectionViewHolder viewHolder = (SectionViewHolder)(convertView == null ? null : convertView.getTag());
        if(viewHolder == null) {
            convertView = layoutInflater.inflate(R.layout.section_title,parent,false);
            viewHolder = new SectionViewHolder();
            viewHolder.sectionTitle = (TextView)convertView.findViewById(R.id.sectionTitleLabel);
            convertView.setTag(viewHolder);
        }
        return convertView;
    }
    private View getGridView(View convertView, ViewGroup parent) {
        GridViewHolder viewHolder = (GridViewHolder)(convertView == null ? null : convertView.getTag());
        if(viewHolder == null) {
            convertView = layoutInflater.inflate(R.layout.layout_gridview_expanded,parent,false);
            viewHolder = new GridViewHolder();
            viewHolder.gridView = (GridView)convertView.findViewById(R.id.gridView);

            convertView.setTag(viewHolder);
        }
        return convertView;
    }
    private class TabsViewHolder {
        Button networkButton;
        Button checkinsButton;
    }
    private class GridViewHolder {
        GridView gridView;
    }
    private class SectionViewHolder {
        TextView sectionTitle;
    }
}
