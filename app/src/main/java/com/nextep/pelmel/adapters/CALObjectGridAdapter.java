package com.nextep.pelmel.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.R;
import com.nextep.pelmel.helpers.Utils;
import com.nextep.pelmel.model.CalObject;
import com.nextep.pelmel.model.User;

import java.util.List;

/**
 * Created by cfondacci on 21/09/15.
 */
public class CALObjectGridAdapter extends BaseAdapter {

    private List<CalObject> calObjects;
    private Context context;
    private LayoutInflater layoutInflater;

    public CALObjectGridAdapter(Context context,  List<CalObject> calObjects) {
        this.context = context;
        this.calObjects = calObjects;
        layoutInflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return calObjects.size();
    }

    @Override
    public Object getItem(int position) {
        return calObjects.get(position);
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

//        GridView.LayoutParams lp = (GridView.LayoutParams)convertView.getLayoutParams();
//        lp.height = lp.width;
//        convertView.setLayoutParams(lp);

        final CalObject obj = calObjects.get(position);
        if(obj.getThumb() != null) {
            PelMelApplication.getImageService().displayImage(obj.getThumb(),false,viewHolder.thumbImage);
        } else {
            PelMelApplication.getImageService().cancelDisplay(viewHolder.thumbImage);
            final Bitmap b = PelMelApplication.getUiService().getNoPhotoFor(obj,false);
            viewHolder.thumbImage.setImageBitmap(b);
        }
        viewHolder.thumbImage.setBorderWidth(0.0f);
        viewHolder.thumbImage.setBorderColor(Utils.getColor(R.color.transparent));
        if(obj instanceof User) {
            if(((User)obj).isOnline()) {
                viewHolder.thumbImage.setBorderWidth(1.0f);
                viewHolder.thumbImage.setBorderColor(Utils.getColor(R.color.online));
            }
        }
        viewHolder.titleLabel.setText(obj.getName());
        return convertView;
    }

    public class ViewHolder {
        RoundedImageView thumbImage;
        TextView titleLabel;
    }
}
