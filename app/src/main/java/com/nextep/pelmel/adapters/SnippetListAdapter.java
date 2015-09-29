package com.nextep.pelmel.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.R;
import com.nextep.pelmel.activities.Refreshable;
import com.nextep.pelmel.helpers.Strings;
import com.nextep.pelmel.model.Image;
import com.nextep.pelmel.providers.CountersProvider;
import com.nextep.pelmel.providers.SnippetInfoProvider;
import com.nextep.pelmel.providers.CountersProviderExtended;
import com.nextep.pelmel.views.SnippetViewHolder;

/**
 * Created by cfondacci on 21/07/15.
 */
public class SnippetListAdapter extends BaseAdapter {


    public static final int VIEW_TYPE_SNIPPET = 0;

    private SnippetInfoProvider infoProvider;
    private Context context;
    private LayoutInflater layoutInflater;
    private Refreshable refreshable;

    public SnippetListAdapter(Context context, Refreshable refreshable, SnippetInfoProvider provider) {
        this.context = context;
        this.refreshable = refreshable;
        this.infoProvider = provider;
        this.layoutInflater =  LayoutInflater.from(context);
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
            convertView = layoutInflater.inflate(
                    R.layout.list_row_snippet_main, null);
            viewHolder = new SnippetViewHolder();
            viewHolder.thumbImage = (ImageView) convertView.findViewById(R.id.thumbImage);
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
            viewHolder.countersContainerView = (LinearLayout)convertView.findViewById(R.id.countersContainerView);

            Strings.setFontFamily(viewHolder.titleLabel);
            Strings.setFontFamily(viewHolder.subtitleLabel);
            Strings.setFontFamily(viewHolder.distanceIntroLabel);
            Strings.setFontFamily(viewHolder.distanceLabel);
            Strings.setFontFamily(viewHolder.hoursBadgeTitleLabel);
            Strings.setFontFamily(viewHolder.hoursBadgeSubtitleLabel);

            if(!infoProvider.hasCustomSnippetView()) {
                final View countersView = layoutInflater.inflate(R.layout.layout_counters,viewHolder.countersContainerView,true);
                viewHolder.likeIconContainerView = (LinearLayout)countersView.findViewById(R.id.likeCounterView);
                viewHolder.likeActionLabel = (TextView)countersView.findViewById(R.id.likeActionLabel);
                viewHolder.likeTitleLabel = (TextView)countersView.findViewById(R.id.likeLabel);
                viewHolder.likeIcon = (ImageView)countersView.findViewById(R.id.likeIcon);

                viewHolder.checkinIconContainerView = (LinearLayout)countersView.findViewById(R.id.checkinCounterView);
                viewHolder.checkinActionLabel = (TextView)countersView.findViewById(R.id.checkinActionLabel);
                viewHolder.checkinTitleLabel = (TextView)countersView.findViewById(R.id.checkinLabel);
                viewHolder.checkinIcon = (ImageView)countersView.findViewById(R.id.checkinIcon);

                viewHolder.chatIconContainerView = (LinearLayout)countersView.findViewById(R.id.chatCounterView);
                viewHolder.chatActionLabel = (TextView)countersView.findViewById(R.id.chatActionLabel);
                viewHolder.chatTitleLabel = (TextView)countersView.findViewById(R.id.chatLabel);
                viewHolder.chatIcon = (ImageView)countersView.findViewById(R.id.chatIcon);

            } else {
                infoProvider.createCustomSnippetView(context,viewHolder.countersContainerView);
            }
            convertView.setTag(viewHolder);
        }
        return convertView;
    }

    private void configureSnippetView(View convertView) {
        // Retrieving holder for our UI widgets
        SnippetViewHolder viewHolder = (SnippetViewHolder) convertView.getTag();


        final Image thumb = infoProvider.getItem() != null ? infoProvider.getItem().getThumb() : null;
        if(thumb!=null) {
            PelMelApplication.getImageService().displayImage(thumb,true,viewHolder.thumbImage);
        } else {
            viewHolder.thumbImage.setImageBitmap(null);
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)viewHolder.thumbImage.getLayoutParams();
            lp.width = 0;
            lp.height = 0;
            viewHolder.thumbImage.setLayoutParams(lp);
        }
        viewHolder.titleLabel.setText(infoProvider.getTitle());
        viewHolder.subtitleLabel.setText(infoProvider.getSubtitle());
        viewHolder.subtitleIcon.setImageBitmap(infoProvider.getSubtitleIcon());
        viewHolder.distanceIntroLabel.setText(infoProvider.getDistanceIntroText());
        viewHolder.distanceLabel.setText(infoProvider.getDistanceText());
        viewHolder.hoursBadgeTitleLabel.setText(infoProvider.getHoursBadgeTitle());
        viewHolder.hoursBadgeSubtitleLabel.setText(infoProvider.getHoursBadgeSubtitle());
        viewHolder.hoursBadgeTitleLabel.setTextColor(infoProvider.getHoursColor());
        viewHolder.hoursBadgeSubtitleLabel.setTextColor(infoProvider.getHoursColor());

        if(!infoProvider.hasCustomSnippetView()) {
            final CountersProvider countersProvider = infoProvider.getCountersProvider();
            viewHolder.likeIcon.setImageBitmap(countersProvider.getCounterImageAtIndex(CountersProvider.COUNTER_LIKE));
            viewHolder.checkinIcon.setImageBitmap(countersProvider.getCounterImageAtIndex(CountersProvider.COUNTER_CHECKIN));
            viewHolder.chatIcon.setImageBitmap(countersProvider.getCounterImageAtIndex(CountersProvider.COUNTER_CHAT));

            viewHolder.likeTitleLabel.setText(countersProvider.getCounterLabelAtIndex(CountersProvider.COUNTER_LIKE));
            viewHolder.checkinTitleLabel.setText(countersProvider.getCounterLabelAtIndex(CountersProvider.COUNTER_CHECKIN));
            viewHolder.chatTitleLabel.setText(countersProvider.getCounterLabelAtIndex(CountersProvider.COUNTER_CHAT));

            viewHolder.likeActionLabel.setText(countersProvider.getCounterActionLabelAtIndex(CountersProvider.COUNTER_LIKE));
            viewHolder.checkinActionLabel.setText(countersProvider.getCounterActionLabelAtIndex(CountersProvider.COUNTER_CHECKIN));
            viewHolder.chatActionLabel.setText(countersProvider.getCounterActionLabelAtIndex(CountersProvider.COUNTER_CHAT));

            viewHolder.likeIconContainerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    countersProvider.executeCounterActionAtIndex(context,refreshable,CountersProvider.COUNTER_LIKE);
                }
            });
            viewHolder.checkinIconContainerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    countersProvider.executeCounterActionAtIndex(context,refreshable,CountersProvider.COUNTER_CHECKIN);
                }
            });
            viewHolder.chatIconContainerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    countersProvider.executeCounterActionAtIndex(context,refreshable,CountersProvider.COUNTER_CHAT);
                }
            });

            // Handling visibility
            int likeVisibility = countersProvider.hasCounter(CountersProvider.COUNTER_LIKE) ? View.VISIBLE : View.INVISIBLE;
            viewHolder.likeIcon.setVisibility(likeVisibility);
            viewHolder.likeActionLabel.setVisibility(likeVisibility);
            viewHolder.likeIconContainerView.setVisibility(likeVisibility);
            viewHolder.likeTitleLabel.setVisibility(likeVisibility);

            int checkinVisibility = countersProvider.hasCounter(CountersProvider.COUNTER_CHECKIN) ? View.VISIBLE : View.INVISIBLE;
            viewHolder.checkinIcon.setVisibility(checkinVisibility);
            viewHolder.checkinActionLabel.setVisibility(checkinVisibility);
            viewHolder.checkinIconContainerView.setVisibility(checkinVisibility);
            viewHolder.checkinTitleLabel.setVisibility(checkinVisibility);

            int chatVisibility = countersProvider.hasCounter(CountersProvider.COUNTER_CHAT) ? View.VISIBLE : View.INVISIBLE;
            viewHolder.chatIcon.setVisibility(chatVisibility);
            viewHolder.chatActionLabel.setVisibility(chatVisibility);
            viewHolder.chatIconContainerView.setVisibility(chatVisibility);
            viewHolder.chatTitleLabel.setVisibility(chatVisibility);

            Resources resources = PelMelApplication.getInstance().getResources();

            int resId = getBackgroundResource(countersProvider, CountersProvider.COUNTER_LIKE);
            viewHolder.likeIconContainerView.setBackgroundResource(resId);

            resId = getBackgroundResource(countersProvider, CountersProvider.COUNTER_CHECKIN);
            viewHolder.checkinIconContainerView.setBackgroundResource(resId);

            resId = getBackgroundResource(countersProvider,CountersProvider.COUNTER_CHAT);
            viewHolder.chatIconContainerView.setBackgroundResource(resId);

        } else {
            infoProvider.refreshCustomSnippetView(context, viewHolder.countersContainerView);
        }
    }

    public int getBackgroundResource(CountersProvider provider, int index) {
        int bgResId = 0;
        if(provider instanceof  CountersProviderExtended) {
            bgResId = ((CountersProviderExtended) provider).getCounterBackgroundResource(index);
        } else {
            if(provider.isCounterSelectedAtIndex(index)) {
                bgResId = R.drawable.bg_counter_selected;
            } else {
                bgResId = R.drawable.bg_counter;
            }
        }
        return bgResId;
    }
}
