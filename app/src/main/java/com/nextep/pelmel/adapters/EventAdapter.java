package com.nextep.pelmel.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.R;
import com.nextep.pelmel.model.Event;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class EventAdapter extends ArrayAdapter<Event> {
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd");

	public EventAdapter(Context context, int textViewResourceId,
			List<Event> objects) {
		super(context, textViewResourceId, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;

		final Event event = getItem(position);

		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.list_row, null);

			viewHolder.title = (TextView) convertView.findViewById(R.id.title);
			viewHolder.distance = (TextView) convertView
					.findViewById(R.id.distance);
			viewHolder.type = (TextView) convertView.findViewById(R.id.type);
			viewHolder.image = (ImageView) convertView
					.findViewById(R.id.list_image);

			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.title.setText(event.getName());

		final StringBuilder buf = new StringBuilder();
		if (event.getStartDate() != null) {
			buf.append(DATE_FORMAT.format(event.getStartDate()));
			buf.append(" - ");
		}
		buf.append(event.getDistanceLabel());
		viewHolder.distance.setText(buf.toString()); // String.format("%s %s",
														// place.getAddress(),
														// place.getCity()));
		viewHolder.type.setText(R.string.eventType);
		viewHolder.image.setImageResource(R.drawable.no_photo);
		if (event.getThumb() != null) {
			PelMelApplication.getImageService().displayImage(event.getThumb(),
					true, viewHolder.image);
		}

		return convertView;
	}

	private class ViewHolder {
		TextView title;
		TextView distance;
		TextView type;
		ImageView image;
	}

}
