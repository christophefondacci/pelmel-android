package com.nextep.pelmel.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nextep.pelmel.R;
import com.nextep.pelmel.providers.SnippetInfoProvider;
import com.nextep.pelmel.views.SnippetViewHolder;

/**
 * Created by cfondacci on 21/07/15.
 */
public class SnippetListAdapter extends BaseAdapter {


    public static final int VIEW_TYPE_SNIPPET = 0;

    private SnippetInfoProvider infoProvider;
    private Context context;
    public SnippetListAdapter(Context context, SnippetInfoProvider provider) {
        this.context = context;
        this.infoProvider = provider;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_SNIPPET;
    }

    @Override
    public Object getItem(int position) {
        return "snippet";
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = getSnippetConvertView(convertView);

        // Configuring
        configureSnippetView(convertView);

        return convertView;
    }


    private View getSnippetConvertView(View convertView) {
        SnippetViewHolder viewHolder;
        if (convertView == null || convertView.getTag() == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.list_row_snippet_main, null);
            viewHolder = new SnippetViewHolder();
            viewHolder.titleLabel= (TextView) convertView
                    .findViewById(R.id.titleLabel);
            viewHolder.subtitleLabel= (TextView) convertView
                    .findViewById(R.id.subtitleLabel);
            viewHolder.subtitleIcon= (ImageView) convertView
                    .findViewById(R.id.subtitleIcon);
            viewHolder.distanceIntroLabel= (TextView) convertView
                    .findViewById(R.id.distanceIntroLabel);
            viewHolder.distanceLabel= (TextView) convertView
                    .findViewById(R.id.distanceLabel);
            viewHolder.hoursBadgeTitleLabel= (TextView) convertView
                    .findViewById(R.id.hoursBadgeTitleLabel);
            viewHolder.hoursBadgeSubtitleLabel= (TextView) convertView
                    .findViewById(R.id.hoursBadgeSubtitleLabel);
            convertView.setTag(viewHolder);
        }
        return convertView;
    }

    private void configureSnippetView(View convertView) {
        // Retrieving holder for our UI widgets
        SnippetViewHolder viewHolder = (SnippetViewHolder) convertView.getTag();
        viewHolder.titleLabel.setText(infoProvider.getTitle());
        viewHolder.subtitleLabel.setText(infoProvider.getSubtitle());
        viewHolder.subtitleIcon.setImageBitmap(infoProvider.getSubtitleIcon());
        viewHolder.distanceIntroLabel.setText(infoProvider.getDistanceIntroText());
        viewHolder.distanceLabel.setText(infoProvider.getDistanceText());
        viewHolder.hoursBadgeTitleLabel.setText(infoProvider.getHoursBadgeTitle());
        viewHolder.hoursBadgeSubtitleLabel.setText(infoProvider.getHoursBadgeSubtitle());
    }
}
