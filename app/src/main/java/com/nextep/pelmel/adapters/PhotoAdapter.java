package com.nextep.pelmel.adapters;

import java.util.Collections;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.R;
import com.nextep.pelmel.model.CalObject;
import com.nextep.pelmel.model.Image;

public class PhotoAdapter extends ArrayAdapter<Image> {

	private final CalObject calObject;

	public PhotoAdapter(Context context, CalObject object) {
		super(context, android.R.layout.simple_list_item_2,
				object != null ? object.getImages() : Collections.EMPTY_LIST);
		this.calObject = object;
	}

	@Override
	public int getCount() {
		return calObject.getImages().size();
	}

	@Override
	public Image getItem(int position) {
		return calObject.getImages().get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Holder of our view widgets
		final ViewHolder viewHolder;
		// First initialization for this row
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.list_row_photo, null);
			viewHolder = new ViewHolder();
			viewHolder.imageView = (ImageView) convertView
					.findViewById(R.id.list_image);
			convertView.setTag(viewHolder);
		} else {
			// Retrieving already loaded row
			viewHolder = (ViewHolder) convertView.getTag();
		}
		// Getting image to display for this row
		final Image media = getItem(position);
		if (media != null) {
			if (media.getThumb() != null) {
				viewHolder.imageView.setImageBitmap(media.getThumb());
			} else {
				PelMelApplication.getImageService().displayImage(media, false,
						viewHolder.imageView);
			}
		}
		return convertView;
	}

	class ViewHolder {
		ImageView imageView;
	}
}
