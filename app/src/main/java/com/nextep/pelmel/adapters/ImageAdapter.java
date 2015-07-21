package com.nextep.pelmel.adapters;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.model.CalObject;
import com.nextep.pelmel.model.Image;

public class ImageAdapter extends BaseAdapter {

	private final Context context;
	private final CalObject calObject;
	private Map<Integer, ImageView> viewsMap = new HashMap<Integer, ImageView>();

	public ImageAdapter(Context context, CalObject place) {
		this.context = context;
		this.calObject = place;
	}

	@Override
	public int getCount() {
		return calObject.getImages().size();
	}

	@Override
	public Object getItem(int position) {
		return calObject.getImages().get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView = viewsMap.get(position);
		if (imageView == null) {
			imageView = new ImageView(context);
			imageView.setScaleType(ScaleType.CENTER_CROP);
			imageView.setLayoutParams(new Gallery.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			imageView.setBackgroundColor(0xFF000000);
			final Image media = (Image) getItem(position);
			if (media != null) {
				if (media.getFullImage() != null) {
					imageView.setImageBitmap(media.getFullImage());
				} else {

					if (media.getThumb() != null) {
						imageView.setImageBitmap(media.getThumb());
					}
					PelMelApplication.getImageService().displayImage(media,
							false, imageView);
				}
			}
			viewsMap.put(position, imageView);
		}
		return imageView;
	}
}
