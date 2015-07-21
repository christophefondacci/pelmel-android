package com.nextep.pelmel.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.R;
import com.nextep.pelmel.model.Tag;
import com.nextep.pelmel.model.User;
import com.nextep.pelmel.services.TagService;

import java.util.List;

public class TagAdapter extends ArrayAdapter<Tag> implements
		OnItemClickListener {

	private final TagService tagService;
	private final User user;

	public TagAdapter(Context context, int textViewResourceId,
					  User currentUser, List<Tag> objects) {
		super(context, textViewResourceId, objects == null ? PelMelApplication
				.getTagService().listTags() : objects);
		tagService = PelMelApplication.getTagService();
		user = currentUser;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;

		Tag tag = getItem(position);

		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.list_row_tags, null);

			viewHolder.row = (RelativeLayout) convertView
					.findViewById(R.id.tagRow);
			viewHolder.tagLabel = (TextView) convertView
					.findViewById(R.id.tagLabel);
			viewHolder.tagIcon = (ImageView) convertView
					.findViewById(R.id.tagIcon);
			viewHolder.tagCheckImage = (ImageView) convertView
					.findViewById(R.id.tagCheckImage);

			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		// Refreshing row content
		refreshRow(viewHolder, tag);
		return convertView;
	}

	private void refreshRow(ViewHolder viewHolder, Tag tag) {
		viewHolder.tagLabel.setText(tag.getLabel());
		final int imageResource = tagService.getImageResource(tag);
		viewHolder.tagIcon.setImageResource(imageResource);
		if (user.getTags().contains(tag)) {
			viewHolder.tagCheckImage.setImageResource(R.drawable.checked);
			viewHolder.row.setBackgroundDrawable(PelMelApplication
					.getInstance().getResources()
					.getDrawable(R.drawable.tab_bg_gradient));
		} else {
			viewHolder.tagCheckImage.setImageResource(R.drawable.unchecked);
			viewHolder.row.setBackgroundDrawable(PelMelApplication
					.getInstance().getResources()
					.getDrawable(R.drawable.list_selector));
		}
	}

	private class ViewHolder {
		RelativeLayout row;
		ImageView tagIcon;
		TextView tagLabel;
		ImageView tagCheckImage;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
							long id) {
		final Tag tag = getItem(position);
		if (user.getTags().contains(tag)) {
			user.removeTag(tag);
		} else {
			user.addTag(tag);
		}
		final ViewHolder viewHolder = (ViewHolder) view.getTag();
		refreshRow(viewHolder, tag);
	}
}
