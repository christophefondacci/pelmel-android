package com.nextep.pelmel.adapters;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.R;
import com.nextep.pelmel.activities.OverviewActivity;
import com.nextep.pelmel.model.CalObject;
import com.nextep.pelmel.model.Image;
import com.nextep.pelmel.model.User;

public class CALObjectThumbAdapter extends BaseAdapter {

	private final Context context;
	private final List<CalObject> calObjects;

	public CALObjectThumbAdapter(Context context, List<CalObject> objects) {
		this.context = context;
		this.calObjects = objects;
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
		ViewHolder viewHolder = null;
		if (convertView == null) {
			// Building a new holder of widgets
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.layout_user, null);

			// Registering widgets into holder object
			viewHolder.thumbImageView = (ImageView) convertView
					.findViewById(R.id.thumb_img);
			viewHolder.title = (TextView) convertView.findViewById(R.id.pseudo);
			viewHolder.onlineStatusView = (ImageView) convertView
					.findViewById(R.id.online_status);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		final CalObject object = (CalObject) getItem(position);
		if (object != null) {
			// Injecting thumbnail into image view
			final Image image = object.getThumb();
			if (image != null) {
				// Adding thumb if already loaded
				if (image.getThumb() != null) {
					viewHolder.thumbImageView.setImageBitmap(image.getThumb());
				} else {
					// Or loading it
					PelMelApplication.getImageService().displayImage(image,
							true, viewHolder.thumbImageView);
				}
			} else {
				// If no photo then we display default image
				if (object instanceof User) {
					// Default no user photo
					viewHolder.thumbImageView
							.setImageResource(R.drawable.no_photo_profile_small);
				} else {
					// Or default photo
					viewHolder.thumbImageView
							.setImageResource(R.drawable.no_photo);
				}
			}
			// Injecting name
			viewHolder.title.setText(object.getName());

			// Handling online / offline status for users
			if (object instanceof User) {
				if (((User) object).isOnline()) {
					viewHolder.onlineStatusView
							.setImageResource(R.drawable.online);
				} else {
					viewHolder.onlineStatusView.setImageBitmap(null); // R.drawable.offline);
				}
			} else {
				viewHolder.onlineStatusView.setImageBitmap(null);
			}

			// Handling tap events
			viewHolder.thumbImageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					final Intent intent = new Intent(context,
							OverviewActivity.class);
					PelMelApplication.setOverviewObject(object);
					context.startActivity(intent);
					// context.overridePendingTransition(R.anim.push_left_in,
					// R.anim.push_left_out);
				}
			});
		}

		return convertView;
	}

	private class ViewHolder {
		ImageView thumbImageView;
		TextView title;
		ImageView onlineStatusView;
	}
}
