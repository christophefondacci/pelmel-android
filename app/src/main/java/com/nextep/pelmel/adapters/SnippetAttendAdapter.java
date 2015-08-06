package com.nextep.pelmel.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nextep.pelmel.R;
import com.nextep.pelmel.model.User;

/**
 * Created by cfondacci on 06/08/15.
 */
public class SnippetAttendAdapter extends SnippetEventsListAdapter {

    private Context context;
    private User user;
    private LayoutInflater layoutInflater;
    public SnippetAttendAdapter(Context context, User user) {
        super(context, user.getEvents());
        this.context = context;
        this.user = user;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return user.getEvents() != null && !user.getEvents().isEmpty() ? 1+ user.getEvents().size() : 0;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? 0 : 1;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if(position == 0) {
            if (convertView == null || convertView.getTag() == null) {
                convertView = layoutInflater.inflate(R.layout.section_title, parent, false);
                convertView.setTag(convertView.findViewById(R.id.sectionTitleLabel));
            }
            final TextView titleLabel = (TextView) convertView.getTag();
            titleLabel.setText(context.getText(R.string.section_attending_events));
            return convertView;
        }
        return super.getView(position-1, convertView, parent);
    }
}
