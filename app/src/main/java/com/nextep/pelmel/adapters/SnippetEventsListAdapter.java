package com.nextep.pelmel.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.R;
import com.nextep.pelmel.helpers.Strings;
import com.nextep.pelmel.model.Event;
import com.nextep.pelmel.model.Image;
import com.nextep.pelmel.views.SnippetPlaceViewHolder;

import java.util.List;

/**
 * Created by cfondacci on 22/07/15.
 */
public class SnippetEventsListAdapter extends BaseAdapter {

    private Context context;
    private List<Event> events;

    public SnippetEventsListAdapter(Context context, List<Event> events) {
        this.context = context;
        this.events = events;
    }
    @Override
    public int getCount() {
        return this.events.size();
    }

    @Override
    public Object getItem(int position) {
        return this.events.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = getSnippetConvertView(convertView);

        // Configuring
        final Event event = events.get(position);
        configureSnippetView(convertView,event);

        return convertView;
    }


    private View getSnippetConvertView(View convertView) {
        SnippetPlaceViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.list_row_cal_object, null);
            viewHolder = new SnippetPlaceViewHolder();
            viewHolder.image= (ImageView) convertView
                    .findViewById(R.id.image);
            viewHolder.countIcon= (ImageView) convertView
                    .findViewById(R.id.countIcon);
            viewHolder.countLabel= (TextView) convertView
                    .findViewById(R.id.countLabel);
            viewHolder.dateLabel= (TextView) convertView
                    .findViewById(R.id.dateLabel);
            viewHolder.titleLabel= (TextView) convertView
                    .findViewById(R.id.titleLabel);
            viewHolder.locationIcon= (ImageView) convertView
                    .findViewById(R.id.locationIcon);
            viewHolder.locationLabel= (TextView) convertView
                    .findViewById(R.id.locationLabel);
            convertView.setTag(viewHolder);
        }
        return convertView;
    }

    private void configureSnippetView(View convertView,Event event) {
        // Retrieving holder for our UI widgets
        SnippetPlaceViewHolder viewHolder = (SnippetPlaceViewHolder) convertView.getTag();
        Image thumb = event.getThumb();
        if(thumb == null) {
            thumb = event.getPlace().getThumb();
        }
        if(thumb!=null) {
            PelMelApplication.getImageService().displayImage(thumb, false, viewHolder.image);
        } else {
            viewHolder.image.setImageBitmap(BitmapFactory.decodeResource(PelMelApplication.getInstance().getResources(), R.drawable.img_blank));
        }
        viewHolder.dateLabel.setText(Strings.getEventDate(event, true));

        // Computing distance
//        final ConversionService convService = PelMelApplication.getConversionService();
//        final double distance = convService.getDistanceTo(event);
//        final String distanceStr = convService.getDistanceStringForMiles(distance);

        viewHolder.titleLabel.setText(Strings.getName(event));

        if(event.getLikeCount()>0) {
            viewHolder.countIcon.setImageBitmap(BitmapFactory.decodeResource(PelMelApplication.getInstance().getResources(), R.drawable.snp_icon_event));
            viewHolder.countLabel.setText(Strings.getCountedText(R.string.counter_event_in_singular, R.string.counter_event_in, event.getLikeCount()));
        } else {
            viewHolder.countIcon.setImageBitmap(null);
            viewHolder.countLabel.setText(null);
        }
        if(event.getPlace() != null && event.getPlace().getCityName() != null) {
            viewHolder.locationLabel.setText(event.getPlace().getName() + ", " + event.getPlace().getCityName());
        } else if(event.getPlace()!=null){
            viewHolder.locationLabel.setText(event.getPlace().getName());
        } else {
            viewHolder.locationLabel.setText(null);
        }

    }
}
