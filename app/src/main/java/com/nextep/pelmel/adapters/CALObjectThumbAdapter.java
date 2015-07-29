package com.nextep.pelmel.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.R;
import com.nextep.pelmel.model.CalObject;
import com.nextep.pelmel.model.Image;
import com.nextep.pelmel.model.User;

import java.util.List;

public class CALObjectThumbAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {

	private final Context context;
	private final List<CalObject> calObjects;
	private final int resSize;
	public CALObjectThumbAdapter(Context context, List<CalObject> objects) {
		this(context,objects,R.dimen.thumbs_default_size);
	}
	public CALObjectThumbAdapter(Context context, List<CalObject> objects, int resSize) {
		this.context = context;
		this.calObjects = objects;
		this.resSize = resSize;
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
			viewHolder.thumbImageView = (RoundedImageView) convertView
					.findViewById(R.id.thumb_img);

			// Adjusting layout parameters
			Resources resources = PelMelApplication.getInstance().getResources();
			final float size =  resources.getDimension(resSize);

			// Sizing image
			LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)viewHolder.thumbImageView.getLayoutParams();
			layoutParams.width = (int)size;
			layoutParams.height = (int)size;
			viewHolder.thumbImageView.setLayoutParams(layoutParams);

			// Adjusting radius
			final CalObject obj = (CalObject)getItem(position);
			if(obj instanceof User) {
				viewHolder.thumbImageView.setCornerRadius(size / 2);
			} else {
				viewHolder.thumbImageView.setCornerRadius(0);
			}

			// Sizing label
			viewHolder.title = (TextView) convertView.findViewById(R.id.pseudo);
			layoutParams = (LinearLayout.LayoutParams)viewHolder.title.getLayoutParams();
			layoutParams.width = (int)size+10;
			viewHolder.title.setLayoutParams(layoutParams);

			// Applying layout
			convertView.requestLayout();

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
					viewHolder.thumbImageView.setBorderColor(PelMelApplication.getInstance().getResources().getColor(R.color.online));
				} else {
					viewHolder.thumbImageView.setBorderColor(PelMelApplication.getInstance().getResources().getColor(R.color.offline));
				}
			} else {
//				viewHolder.onlineStatusView.setImageBitmap(null);
			}

			// Handling tap events
//			viewHolder.thumbImageView.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					final Intent intent = new Intent(context,
//							OverviewActivity.class);
//					PelMelApplication.setOverviewObject(object);
//					context.startActivity(intent);
//					// context.overridePendingTransition(R.anim.push_left_in,
//					// R.anim.push_left_out);
//				}
//			});
		}

		return convertView;
	}

	private class ViewHolder {
		RoundedImageView thumbImageView;
		TextView title;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		final CalObject obj = calObjects.get(position);
		PelMelApplication.getSnippetContainerSupport().showSnippetFor(obj,false,false);
	}
}
