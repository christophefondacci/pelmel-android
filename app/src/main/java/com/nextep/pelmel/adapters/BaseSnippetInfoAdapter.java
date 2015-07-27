package com.nextep.pelmel.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nextep.pelmel.R;
import com.nextep.pelmel.helpers.Strings;

/**
 * Created by cfondacci on 25/07/15.
 */
public abstract class BaseSnippetInfoAdapter extends BaseAdapter {


    private Context context;
    private LayoutInflater layoutInflater;

    public BaseSnippetInfoAdapter(Context context) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    protected Context getContext() {
        return context;
    }
    /**
     * Provides the text of this info line
     * @param position index of the line to retrieve
     * @return the info text to display
     */
    protected abstract String getInfoText(int position);

    /**
     * Provides an optional image / icon to display on the left of the text.
     *
     * @param position index of the line
     * @return the icon as a Bitmap or <code>null</code> if no icon
     */
    protected abstract Bitmap getInfoImage(int position);

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_row_info,parent,false);
        }
        ViewHolder viewHolder = (ViewHolder)convertView.getTag();
        if(viewHolder == null) {
            viewHolder = new ViewHolder();
            viewHolder.infoLabel = (TextView)convertView.findViewById(R.id.infoLabel);
            viewHolder.infoImage = (ImageView)convertView.findViewById(R.id.infoImage);
            convertView.setTag(viewHolder);
        }

        final String infoText = getInfoText(position);
        final Bitmap infoImage = getInfoImage(position);
        viewHolder.infoLabel.setText(infoText);
        viewHolder.infoLabel.setTextColor(context.getResources().getColor(R.color.white));
        viewHolder.infoImage.setImageBitmap(infoImage);

        Strings.setFontFamily(viewHolder.infoLabel);
        return convertView;
    }

    private class ViewHolder {
        TextView infoLabel;
        ImageView infoImage;
    }
}
