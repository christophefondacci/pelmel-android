package com.nextep.pelmel.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.R;
import com.nextep.pelmel.dialogs.SelectImageDialogFragment;
import com.nextep.pelmel.listeners.ImageRemovalCallback;
import com.nextep.pelmel.model.CalObject;
import com.nextep.pelmel.model.Image;
import com.nextep.pelmel.model.User;
import com.nextep.pelmel.services.ImageService;

public class OldPhotoAdapter extends ArrayAdapter<Image> {

	private final ImageService imageLoaderService;
	private final FragmentManager manager;
	private boolean removeEnabled = false;
	private final CalObject parent;
	private final User user;
	private final ImageRemovalCallback removalCallback;
	private final boolean firstLoad = true; // To workaround android bug

	public OldPhotoAdapter(Context context, FragmentManager fragmentManager,
			List<Image> images, CalObject parent, User user,
			ImageRemovalCallback removalCallback) {
		super(context, android.R.layout.simple_list_item_2,
				buildListFromImages(images));
		manager = fragmentManager;
		this.parent = parent;
		this.user = user;
		this.removalCallback = removalCallback;

		// Defining our services
		imageLoaderService = PelMelApplication.getImageService();
	}

	private static List<Image> buildListFromImages(List<Image> images) {
		final List<Image> internalImages = new ArrayList<Image>(images);
		// Inserting object for first "controls" row
		internalImages.add(0, null);
		return internalImages;
	}

	@Override
	public int getItemViewType(int position) {
		return position == 0 ? 0 : 1;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public View getView(int position, View c, ViewGroup group) {
		ViewHolder viewHolder;
		View convertView = c;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			if (position > 0) {
				// This is a photo row
				convertView = LayoutInflater.from(getContext()).inflate(
						R.layout.list_row_photo, null);

				// Workaround for ANDROID bug (tiring)
				//
				if (firstLoad) {
					convertView.postDelayed(new Runnable() {

						@Override
						public void run() {
							notifyDataSetChanged();
						}
					}, 200);
				}
				// Binding controls to our local view holder
				viewHolder.thumbnailImageView = (ImageView) convertView
						.findViewById(R.id.list_image);
				viewHolder.textView = (TextView) convertView
						.findViewById(R.id.title);
				viewHolder.removeButton = (Button) convertView
						.findViewById(R.id.removeButton);
			} else {
				// This is our top controls row
				convertView = LayoutInflater.from(getContext()).inflate(
						R.layout.list_row_addremove, null);

				// Binding add/remove controls to our local view holder
				viewHolder.addButton = (Button) convertView
						.findViewById(R.id.addButton);
				viewHolder.removeButton = (Button) convertView
						.findViewById(R.id.removeButton);
			}
			convertView.setTag(viewHolder);
		} else {
			// If existing our view controls holder is the tag of our convert
			// view
			viewHolder = (ViewHolder) convertView.getTag();
		}

		final Image image = getItem(position);
		if (image != null) {
			imageLoaderService.displayImage(image, true,
					viewHolder.thumbnailImageView);
			viewHolder.textView.setText(position == 1 ? "Main photo" : "Photo "
					+ position);
			viewHolder.removeButton.setVisibility(removeEnabled ? View.VISIBLE
					: View.INVISIBLE);
			viewHolder.removeButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					imageLoaderService.removeImage(image, parent, user,
							removalCallback);
				}
			});
		} else {
			if (viewHolder.addButton != null) {
				viewHolder.addButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						final SelectImageDialogFragment selectDialog = new SelectImageDialogFragment();
						selectDialog.show(manager, "PHOTO");
					}
				});
			}
			if (viewHolder.removeButton != null) {
				viewHolder.removeButton
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								removeEnabled = !removeEnabled;
								notifyDataSetChanged();
							}
						});
			}
		}
		// Returning our view
		return convertView;
	}

	private class ViewHolder {
		ImageView thumbnailImageView;
		TextView textView;
		Button removeButton;
		Button addButton;
	}

}
