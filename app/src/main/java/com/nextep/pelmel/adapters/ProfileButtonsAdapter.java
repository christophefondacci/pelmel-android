package com.nextep.pelmel.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.R;
import com.nextep.pelmel.activities.LoginActivity;

/**
 * Created by cfondacci on 07/08/15.
 */
public class ProfileButtonsAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater layoutInflater;

    public ProfileButtonsAdapter(Context context) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return 1;
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
        ViewHolder viewHolder;
        if(convertView == null || convertView.getTag() == null) {
            convertView = layoutInflater.inflate(R.layout.list_row_button,parent, false);

            viewHolder = new ViewHolder();
            viewHolder.buttonLabel = (TextView)convertView.findViewById(R.id.buttonLabel);
            viewHolder.buttonIcon = (ImageView)convertView.findViewById(R.id.buttonIcon);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.buttonIcon.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),R.drawable.mnu_icon_disconnect));
        viewHolder.buttonLabel.setText(R.string.disconnect);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PelMelApplication.getUserService().logout();
                Intent intent = new Intent(context, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        return convertView;
    }

    class ViewHolder {
        TextView buttonLabel;
        ImageView buttonIcon;
    }
}
