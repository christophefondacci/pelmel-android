package com.nextep.pelmel.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.nextep.pelmel.R;
import com.nextep.pelmel.helpers.Strings;

/**
 * Created by cfondacci on 21/09/15.
 */
public class NetworkActionsGridAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater layoutInflater;

    public NetworkActionsGridAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return 2;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = (ViewHolder)(convertView == null ? null : convertView.getTag());
        if(viewHolder == null) {
            convertView = layoutInflater.inflate(R.layout.grid_thumb,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.thumbImage = (RoundedImageView)convertView.findViewById(R.id.thumbImage);
            viewHolder.titleLabel = (TextView)convertView.findViewById(R.id.titleLabel);
            convertView.setTag(viewHolder);
        }
        viewHolder.thumbImage.setImageResource(R.drawable.btn_network_chat);
        viewHolder.titleLabel.setText(Strings.getText(R.string.network_action_groupChat));
        return convertView;
    }

    public class ViewHolder {
        RoundedImageView thumbImage;
        TextView titleLabel;
    }
}
