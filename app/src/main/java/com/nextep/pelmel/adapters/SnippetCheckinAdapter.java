package com.nextep.pelmel.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.R;
import com.nextep.pelmel.model.User;

import java.util.Arrays;
import java.util.Collections;

/**
 * Created by cfondacci on 03/08/15.
 */
public class SnippetCheckinAdapter extends SnippetPlacesListAdapter {

    private User user;
    private LayoutInflater layoutInflater ;
    private Context context;
    public  SnippetCheckinAdapter(Context context, User user) {
        super(context,user.getLastLocation() != null ? Arrays.asList(user.getLastLocation()) : Collections.EMPTY_LIST);
        this.user = user;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return PelMelApplication.getUserService().isCheckedInAt(user.getLastLocation()) ? 2 : 0;
    }

    @Override
    public Object getItem(int position) {
        if(position == 0) {
            return null;
        } else {
            return super.getItem(position - 1);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(position == 0) {
            if (convertView == null || convertView.getTag() == null) {
                convertView = layoutInflater.inflate(R.layout.section_title, parent, false);
                convertView.setTag(convertView.findViewById(R.id.sectionTitleLabel));
            }
            final TextView titleLabel = (TextView) convertView.getTag();
            titleLabel.setText(context.getText(R.string.section_current_checkin));
            return convertView;
        } else {
            return super.getView(position-1,convertView,parent);
        }
    }
}
