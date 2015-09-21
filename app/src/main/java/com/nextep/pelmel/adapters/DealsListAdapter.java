package com.nextep.pelmel.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.R;
import com.nextep.pelmel.helpers.PelmelFont;
import com.nextep.pelmel.helpers.Strings;
import com.nextep.pelmel.model.Deal;
import com.nextep.pelmel.model.Image;

import java.util.List;

/**
 * Created by cfondacci on 18/09/15.
 */
public class DealsListAdapter extends BaseAdapter {

    private Context context;
    private List<Deal> deals;
    private LayoutInflater layoutInflater;

    public DealsListAdapter(Context context, List<Deal> deals) {
        this.context = context;
        this.deals = deals;
        this.layoutInflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return deals.size();
    }

    @Override
    public Object getItem(int position) {
        return deals.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = (ViewHolder)(convertView == null ? null : convertView.getTag());
        if(viewHolder == null) {
            convertView = layoutInflater.inflate(R.layout.layout_row_deal,parent, false);
            viewHolder = new ViewHolder();
            viewHolder.placeLabel = (TextView)convertView.findViewById(R.id.placeLabel);
            viewHolder.dealLabel = (TextView)convertView.findViewById(R.id.dealLabel);
            viewHolder.placeImage= (ImageView)convertView.findViewById(R.id.placeImage);
            viewHolder.useDealButton= (Button)convertView.findViewById(R.id.useDealButton);

            Strings.setFontFamily(viewHolder.placeLabel, PelmelFont.SOURCE_SANSPRO_LIGHT);
            Strings.setFontFamily(viewHolder.dealLabel, PelmelFont.SOURCE_SANSPRO_LIGHT);
            Strings.setFontFamily(viewHolder.useDealButton, PelmelFont.SOURCE_SANSPRO_LIGHT);
            convertView.setTag(viewHolder);
        }

        final Deal deal = deals.get(position);
        viewHolder.placeLabel.setText(deal.getRelatedObject().getName());
        final String dealTitle = PelMelApplication.getDealService().getDealTitle(deal);
        viewHolder.dealLabel.setText(dealTitle);

        // Displaying image
        final Image placeImage = deal.getRelatedObject().getThumb();
        if(placeImage == null) {
            viewHolder.placeImage.setImageBitmap(PelMelApplication.getUiService().getNoPhotoFor(deal.getRelatedObject(), false));
        } else {
            PelMelApplication.getImageService().displayImage(deal.getRelatedObject().getThumb(), false, viewHolder.placeImage);
        }

        final AbsListView.LayoutParams params = (AbsListView.LayoutParams)convertView.getLayoutParams();
        if(!PelMelApplication.getLocalizationService().isCheckinEnabled(deal.getRelatedObject())) {
            viewHolder.useDealButton.setVisibility(View.INVISIBLE);
            params.height = (int)PelMelApplication.getInstance().getResources().getDimension(R.dimen.deal_list_height_nousedeal);
        } else {
            params.height = (int)PelMelApplication.getInstance().getResources().getDimension(R.dimen.deal_list_height_usedeal);
        }
        convertView.setLayoutParams(params);

        return convertView;
    }

    private class ViewHolder {
        TextView placeLabel;
        TextView dealLabel;
        ImageView placeImage;
        Button useDealButton;
    }
}
