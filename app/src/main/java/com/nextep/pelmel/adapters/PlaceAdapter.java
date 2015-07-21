package com.nextep.pelmel.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.R;
import com.nextep.pelmel.listeners.SortChangeListener;
import com.nextep.pelmel.model.CellType;
import com.nextep.pelmel.model.Place;
import com.nextep.pelmel.model.Refreshable;
import com.nextep.pelmel.model.SortType;
import com.nextep.pelmel.model.Tag;
import com.nextep.pelmel.services.TagService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaceAdapter extends ArrayAdapter<Object> {

	private static final String LOG_TAG = "PlaceAdapter";
	private static final String PLACE_TYPE_PREFIX = "placeType.";
	private static final int VIEW_TYPE_SORTER_CELL = 0;
	private static final int VIEW_TYPE_PLACE_CELL = 1;
	private static final int VIEW_TYPE_RADIUS_CELL = 2;
	private static final int VIEW_TYPE_SEARCH_CELL = 3;
	private static final int VIEW_TYPE_NO_LOCALIZATION_CELL = 4;

	private List<Object> places = Collections.emptyList();
	private Map<Integer, Runnable> imageLoadersMap = new HashMap<Integer, Runnable>();
	private final SortChangeListener sortListener;
	private final Refreshable refreshableParent;
	private int radius = 40;

	public PlaceAdapter(Context context, int textViewResourceId,
						List<Object> objects, SortChangeListener listener,
						Refreshable parent) {
		super(context, textViewResourceId, objects);
		this.places = objects;
		this.sortListener = listener;
		this.refreshableParent = parent;
		radius = PelMelApplication.getUserService().getSearchRadius();
	}

	@Override
	public int getViewTypeCount() {
		return 5;
	}

	@Override
	public int getItemViewType(int position) {
		final Object o = getItem(position);
		if (o == CellType.SORT_CELL) {
			return VIEW_TYPE_SORTER_CELL;
		} else if (o == CellType.NO_LOCALIZATION_CELL
				|| o == CellType.NO_GOOGLE_SERVICES_CELL) {
			return VIEW_TYPE_NO_LOCALIZATION_CELL;
		} else if (o == CellType.RADIUS_CELL) {
			return VIEW_TYPE_RADIUS_CELL;
		} else if (o == CellType.SEARCH_CELL) {
			return VIEW_TYPE_RADIUS_CELL;
		} else {
			return VIEW_TYPE_PLACE_CELL;
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;

		final Object object = getItem(position);

		if (object == CellType.SORT_CELL) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.list_row_sort_refresh, null);

			final Button nearbyButton = (Button) convertView
					.findViewById(R.id.btn_sort_distance);
			final Button popularButton = (Button) convertView
					.findViewById(R.id.btn_sort_popular);
			final Button alphaButton = (Button) convertView
					.findViewById(R.id.btn_sort_alphabetical);

			handleSort(alphaButton, SortType.ALPHABETICAL);
			handleSort(popularButton, SortType.POPULAR);
			handleSort(nearbyButton, SortType.NEARBY);

			final ImageView refresh = (ImageView) convertView
					.findViewById(R.id.img_refresh);
			refresh.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (refreshableParent != null) {
						refreshableParent.refresh();
					}
				}
			});
		} else if (object == CellType.SEARCH_CELL) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.list_row_search, null);

			final Button searchButton = (Button) convertView
					.findViewById(R.id.search_button);
			final TextView searchText = (TextView) convertView
					.findViewById(R.id.search_text);

			searchButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					refreshableParent.refresh(searchText.getText());
				}
			});
		} else if (object == CellType.NO_LOCALIZATION_CELL
				|| object == CellType.NO_GOOGLE_SERVICES_CELL) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.list_row_no_geoloc, null);
			final ImageView refresh = (ImageView) convertView
					.findViewById(R.id.img_refresh);
			final TextView msgText = (TextView) convertView
					.findViewById(R.id.info_label);
			if (object == CellType.NO_LOCALIZATION_CELL) {
				msgText.setText(getContext().getText(
						R.string.search_no_localization));
			} else if (object == CellType.NO_GOOGLE_SERVICES_CELL) {
				msgText.setText(getContext().getText(
						R.string.search_no_google_services));
			}
			refresh.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// Adjusting radius
					PelMelApplication.getUserService().setSearchRadius(radius);
					// Refreshing
					refreshableParent.refresh(radius);
				}
			});
		} else if (object == CellType.RADIUS_CELL) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.list_row_extend_search, null);

			final SeekBar seekBar = (SeekBar) convertView
					.findViewById(R.id.radius_slider);
			final ImageView refresh = (ImageView) convertView
					.findViewById(R.id.img_refresh);
			final TextView radiusText = (TextView) convertView
					.findViewById(R.id.radius_label);
			seekBar.setMax(1500);
			setRadius(radiusText, radius);

			seekBar.setProgress(radius == 0 ? 100 : radius);

			refresh.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// Adjusting radius
					PelMelApplication.getUserService().setSearchRadius(radius);
					// Refreshing
					refreshableParent.refresh(radius);
				}
			});
			seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {

				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {

				}

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
											  boolean fromUser) {
					setRadius(radiusText, progress);
				}
			});
		} else {
			final Place place = (Place) object;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(getContext()).inflate(
						R.layout.list_row, null);

				viewHolder.title = (TextView) convertView
						.findViewById(R.id.title);
				viewHolder.distance = (TextView) convertView
						.findViewById(R.id.distance);
				viewHolder.type = (TextView) convertView
						.findViewById(R.id.type);
				viewHolder.image = (ImageView) convertView
						.findViewById(R.id.list_image);
				viewHolder.tagImage1 = (ImageView) convertView
						.findViewById(R.id.tagImage1);
				viewHolder.tagImage2 = (ImageView) convertView
						.findViewById(R.id.tagImage2);
				viewHolder.tagImage3 = (ImageView) convertView
						.findViewById(R.id.tagImage3);
				viewHolder.likeCounter = (TextView) convertView
						.findViewById(R.id.likeCounter);

				convertView.setTag(viewHolder);

			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			if (viewHolder != null) {
				viewHolder.title.setText(place.getName());

				final String distanceLabel = PelMelApplication
						.getLocalizationService()
						.getLocalizedDistanceTo(
								place,
								getContext().getResources().getConfiguration().locale);
				viewHolder.distance.setText(distanceLabel); // String.format("%s %s",
				// place.getAddress(),
				// place.getCity()));

				final int placeTypeLabel = PelMelApplication.getUiService()
						.getLabelForPlaceType(place.getType());
				viewHolder.type.setText(getContext().getString(placeTypeLabel)
						.toUpperCase());
				final int typeColor = PelMelApplication.getUiService()
						.getColorForPlaceType(place.getType());
				viewHolder.type.setBackgroundResource(typeColor);
				viewHolder.tagImage1.setImageDrawable(null);
				viewHolder.tagImage2.setImageDrawable(null);
				viewHolder.tagImage3.setImageDrawable(null);
				viewHolder.likeCounter.setText(String.valueOf(place
						.getLikeCount()));
				final List<Tag> tags = place.getTags();
				final Resources r = PelMelApplication.getInstance()
						.getResources();
				final TagService tagService = PelMelApplication.getTagService();
				if (tags.size() > 0) {
					viewHolder.tagImage1.setImageDrawable(r
							.getDrawable(tagService.getImageResource(tags
									.get(0))));
				}

				if (tags.size() > 1) {
					viewHolder.tagImage2.setImageDrawable(r
							.getDrawable(tagService.getImageResource(tags
									.get(1))));
				}

				if (tags.size() > 2) {
					viewHolder.tagImage3.setImageDrawable(r
							.getDrawable(tagService.getImageResource(tags
									.get(2))));
				}

				viewHolder.image
						.setImageResource(R.drawable.no_photo_landscape2);
				if (place.getThumb() != null) {
					// ImageLoader.getInstance().displayImage(
					// place.getThumb().getThumbUrl(), viewHolder.image);
					final ViewHolder holder = viewHolder;
					imageLoadersMap.put(position, new Runnable() {

						@Override
						public void run() {
							PelMelApplication.getImageService().displayImage(
									place.getThumb(), false, holder.image);
						}
					});
				}
			}
		}
		return convertView;
	}

	public void loadImages(int firstPosition, int lastPosition) {
		for (int i = firstPosition; i <= lastPosition; i++) {
			final Runnable runnable = imageLoadersMap.get(i);
			if (runnable != null) {
				runnable.run();
			}
		}
	}

	private class ViewHolder {
		TextView title;
		TextView distance;
		TextView type;
		ImageView image;
		ImageView tagImage1;
		ImageView tagImage2;
		ImageView tagImage3;
		TextView likeCounter;
	}

	private void handleSort(View v, final SortType sortType) {
		v.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sortListener.sortChanged(sortType);
			}
		});
	}

	private void setRadius(TextView radiusText, int progress) {
		final float radiusKm = progress * 1.6093f;
		radiusText.setText(Math.round(radiusKm) + " km / " + progress
				+ " miles");
		radius = progress;
	}
}
