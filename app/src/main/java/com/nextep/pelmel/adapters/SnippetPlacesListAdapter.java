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
import com.nextep.pelmel.model.Place;
import com.nextep.pelmel.services.ConversionService;
import com.nextep.pelmel.views.SnippetPlaceViewHolder;

import java.util.List;

/**
 * Created by cfondacci on 22/07/15.
 */
public class SnippetPlacesListAdapter extends BaseAdapter {

    private Context context;
    private List<Place> places;
    private LayoutInflater layoutInflater;

    public SnippetPlacesListAdapter(Context context, List<Place> places) {
        this.context = context;
        this.places = places;
        layoutInflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return this.places.size();
    }

    @Override
    public Object getItem(int position) {
        return this.places.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = getSnippetConvertView(convertView);

        // Configuring
        final Place place = places.get(position);
        configureSnippetView(convertView,place);

        return convertView;
    }


    private View getSnippetConvertView(View convertView) {
        SnippetPlaceViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(
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
            Strings.setFontFamily(viewHolder.countLabel);
            Strings.setFontFamily(viewHolder.dateLabel);
            Strings.setFontFamily(viewHolder.titleLabel);
            Strings.setFontFamily(viewHolder.locationLabel);

            viewHolder.titleLabel.setHorizontallyScrolling(true);
            viewHolder.dateLabel.setHorizontallyScrolling(true);
            convertView.setTag(viewHolder);
        }
        return convertView;
    }

    private void configureSnippetView(View convertView,Place place) {
        // Retrieving holder for our UI widgets
        SnippetPlaceViewHolder viewHolder = (SnippetPlaceViewHolder) convertView.getTag();
        if(place.getThumb()!=null) {
            PelMelApplication.getImageService().displayImage(place.getThumb(), false, viewHolder.image);
        } else {
            viewHolder.image.setImageBitmap(BitmapFactory.decodeResource(PelMelApplication.getInstance().getResources(), R.drawable.img_blank));
        }
        viewHolder.dateLabel.setText(place.getName());

        // Computing distance
        final ConversionService convService = PelMelApplication.getConversionService();
        final double distance = convService.getDistanceTo(place);
        final String distanceStr = convService.getDistanceStringForMiles(distance);

        final String placeTypeLabel = Strings.getText(PelMelApplication.getUiService().getLabelForPlaceType(place.getType()));
        viewHolder.titleLabel.setText(distanceStr + " - " + placeTypeLabel);
        viewHolder.countIcon.setImageBitmap(BitmapFactory.decodeResource(PelMelApplication.getInstance().getResources(), R.drawable.snp_icon_like_white));
        viewHolder.countLabel.setText(Strings.getCountedText(R.string.counter_likes_singular,R.string.counter_likes,place.getLikeCount()));
        viewHolder.locationLabel.setText(place.getCityName());

    }
}
