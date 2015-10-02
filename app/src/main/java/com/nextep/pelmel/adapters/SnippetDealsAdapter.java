package com.nextep.pelmel.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.R;
import com.nextep.pelmel.helpers.PelmelFont;
import com.nextep.pelmel.helpers.Strings;
import com.nextep.pelmel.model.Action;
import com.nextep.pelmel.model.Deal;
import com.nextep.pelmel.model.Place;
import com.nextep.pelmel.model.support.SnippetContainerSupport;

/**
 * Created by cfondacci on 14/09/15.
 */
public class SnippetDealsAdapter extends BaseAdapter {

    private Context context;
    private Place place;
    private LayoutInflater layoutInflater;
    private SnippetContainerSupport snippetContainerSupport;
    public SnippetDealsAdapter(Context context, Place place, SnippetContainerSupport snippetContainerSupport) {
        this.context = context;
        this.place = place;
        this.layoutInflater = LayoutInflater.from(context);
        this.snippetContainerSupport = snippetContainerSupport;
    }
    @Override
    public int getCount() {
        return place.getDeals().size();
    }

    @Override
    public Object getItem(int position) {
        return place.getDeals().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = convertView == null ? null : (ViewHolder)convertView.getTag();
        if(holder == null) {
            convertView = layoutInflater.inflate(R.layout.list_row_snippet_deal,parent, false);

            holder = new ViewHolder();
            holder.dealTitle = (TextView)convertView.findViewById(R.id.dealTitle);
            holder.dealConditionLabel= (TextView)convertView.findViewById(R.id.dealConditionLabel);
            holder.useDealButton = (Button)convertView.findViewById(R.id.useDealButton);
            Strings.setFontFamily(holder.dealTitle, PelmelFont.SOURCE_SANSPRO_LIGHT);
            Strings.setFontFamily(holder.dealConditionLabel, PelmelFont.SOURCE_SANSPRO_LIGHT);
            Strings.setFontFamily(holder.useDealButton, PelmelFont.SOURCE_SANSPRO_LIGHT);
            convertView.setTag(holder);
        }

        int resIdTitle = -1;
        final Deal deal = place.getDeals().get(position);
        switch(deal.getDealType()) {
            case TWO_FOR_ONE:
            default:
                resIdTitle = R.string.deal_type_TWO_FOR_ONE;
                break;
        }
        holder.dealTitle.setText(resIdTitle);
        final String condition = PelMelApplication.getDealService().getDealConditionLabel(deal);
        holder.dealConditionLabel.setText(condition);
        holder.useDealButton.setText(Strings.getText(R.string.deal_use_button));
        holder.useDealButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               PelMelApplication.getActionManager().executeAction(Action.USE_DEAL,deal);
            }
        });
        return convertView;
    }

    private class ViewHolder {
        TextView dealTitle;
        TextView dealConditionLabel;
        Button useDealButton;
    }
}
