package com.nextep.pelmel.activities;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.R;
import com.nextep.pelmel.adapters.CALObjectThumbAdapter;
import com.nextep.pelmel.adapters.ImageAdapter;
import com.nextep.pelmel.listeners.ImageUploadCallback;
import com.nextep.pelmel.listeners.LikeCallback;
import com.nextep.pelmel.listeners.OverviewListener;
import com.nextep.pelmel.listeners.UserListener;
import com.nextep.pelmel.model.CalObject;
import com.nextep.pelmel.model.Image;
import com.nextep.pelmel.model.Tag;
import com.nextep.pelmel.model.User;
import com.nextep.pelmel.providers.OverviewProvider;
import com.nextep.pelmel.providers.ThumbsBoxProvider;
import com.nextep.pelmel.services.DataService;
import com.nextep.pelmel.services.ImageService;
import com.nextep.pelmel.services.TagService;
import com.nextep.pelmel.views.HorizontalListView;

public class OverviewActivity extends MainActionBarActivity implements
		OverviewListener, UserListener, ImageUploadCallback, LikeCallback {

	private static final String TAG_OVERVIEW = "OVERVIEW";
	private Gallery gallery;
	private HorizontalListView likesGallery;
	private HorizontalListView topGallery;
	private TextView title;
	private TextView type;
	private TextView address;
	private TextView likesText;
	private Button addPhotoButton;
	private Button likeButton;
	private ProgressBar progress;
	private ImageView tag1;
	private ImageView tag2;
	private ImageView tag3;

	private View layoutOverviewTop;
	private View layoutDescriptionsBox;
	private View layoutLikes;
	private View layoutThumbsBottom;
	private View layoutThumbsTop;

	private OverviewProvider provider;
	private ImageService imageService;
	private DataService dataService;
	private User user;

	private boolean isHidden = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
		setContentView(R.layout.activity_overview);
		// Getting data provider
		provider = PelMelApplication.getOverviewProvider();
		imageService = PelMelApplication.getImageService();
		dataService = PelMelApplication.getDataService();

		title = (TextView) findViewById(R.id.ov_title);
		type = (TextView) findViewById(R.id.ov_type);
		address = (TextView) findViewById(R.id.ov_address);
		// ImageView image = (ImageView) findViewById(R.id.ov_image);
		gallery = (Gallery) findViewById(R.id.ov_gallery);
		likesGallery = (HorizontalListView) findViewById(R.id.gallery_likes);
		topGallery = (HorizontalListView) findViewById(R.id.gallery_top);
		layoutOverviewTop = findViewById(R.id.opacityFilter);
		layoutDescriptionsBox = findViewById(R.id.ov_description);
		layoutLikes = findViewById(R.id.ov_like_layout);
		layoutThumbsBottom = findViewById(R.id.ov_thumb_bottom);
		layoutThumbsTop = findViewById(R.id.ov_thumb_top);

		likesText = (TextView) findViewById(R.id.likesCountText);
		addPhotoButton = (Button) findViewById(R.id.addPhotoButton);
		progress = (ProgressBar) findViewById(R.id.overviewProgressBar);
		provider.prepareButton(addPhotoButton, this);
		likeButton = (Button) findViewById(R.id.likeButton);
		likeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				progress.setVisibility(View.VISIBLE);
				dataService.like(user, provider.getOverviewObject(),
						OverviewActivity.this);
			}
		});
		updateTags();
		// Handling event that hides / shows all controls when user taps the
		// image
		gallery.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				isHidden = !isHidden;
				updateVisibility();
			}
		});
		updateData();
		final CalObject o = provider.getOverviewObject();

		// Setting adapter for gallery image initialization
		gallery.setAdapter(new ImageAdapter(this, o));

		// Setting adapter for likes management
		// likesGallery.setAdapter(new CALObjectThumbAdapter(this, o.get))

		if (provider != null) {
			PelMelApplication.getUserService().getCurrentUser(this);
		}

	}

	@Override
	public void userInfoAvailable(final User user) {
		this.user = user;
		final CalObject o = provider.getOverviewObject();
		// If we don't have overview data, we load it asynchronously, else
		// we display it
		if (!o.isOverviewDataLoaded()) {
			new AsyncTask<Void, Void, Void>() {

				@Override
				protected Void doInBackground(Void... params) {
					try {
						progress.setVisibility(View.VISIBLE);
					} catch (final Exception e) {
						Log.e(TAG_OVERVIEW,
								"Problems while showing progress : "
										+ e.getMessage(), e);
					}
					PelMelApplication.getDataService()
							.getOverviewData(user,
									provider.getOverviewObject(),
									OverviewActivity.this);
					return null;
				}

			}.execute();
		} else {
			setupOverviewData();
		}
	}

	@Override
	public void userInfoUnavailable() {
		// TODO Auto-generated method stub

	}

	private void updateData() {
		if (provider != null) {
			final CalObject o = provider.getOverviewObject();

			// Updating information
			title.setText(provider.getTitle());
			type.setText(provider.getSubtitle());
			address.setText(provider.getLocationInfo());
			likesText.setText(String.valueOf(provider.getLikes()));

			updateTags();
			// switch(o.getTags().size()) {
			// case 1:
			//
			// }
		}
	}

	private void updateTags() {
		tag1 = (ImageView) findViewById(R.id.tagImage1);
		tag2 = (ImageView) findViewById(R.id.tagImage2);
		tag3 = (ImageView) findViewById(R.id.tagImage3);
		tag1.setImageDrawable(null);
		tag2.setImageDrawable(null);
		tag3.setImageDrawable(null);
		final List<Tag> tags = provider.getTags();
		final Resources r = PelMelApplication.getInstance().getResources();
		final TagService tagService = PelMelApplication.getTagService();
		if (tags.size() > 0) {
			tag1.setImageDrawable(r.getDrawable(tagService
					.getImageResource(tags.get(0))));
		}

		if (tags.size() > 1) {
			tag2.setImageDrawable(r.getDrawable(tagService
					.getImageResource(tags.get(1))));
		}

		if (tags.size() > 2) {
			tag3.setImageDrawable(r.getDrawable(tagService
					.getImageResource(tags.get(2))));
		}
	}

	private void setupOverviewData() {
		if (provider != null) {
			Log.d("OV", "Overview data translation");
			// Updating data
			updateData();

			// Preparing description update
			final View view = findViewById(R.id.ov_description);
			final TextView descText = (TextView) findViewById(R.id.ov_desc_text);
			descText.setText(provider.getDescription());

			// We make it appear by fading in
			view.setVisibility(isHidden ? View.INVISIBLE : View.VISIBLE);
			// AlphaAnimation animation = new AlphaAnimation(0, 1);
			// animation.setDuration(500);
			// animation.setFillAfter(true);
			// animation.setAnimationListener(new AnimationListener() {
			//
			// @Override
			// public void onAnimationStart(Animation animation) {
			// // First we set it to visible
			// view.setVisibility(View.VISIBLE);
			// }
			//
			// @Override
			// public void onAnimationRepeat(Animation animation) {
			// }
			//
			// @Override
			// public void onAnimationEnd(Animation animation) {
			// }
			// });
			// view.startAnimation(animation);

			// Bottom Thumb box
			final ThumbsBoxProvider thumbsBottomProvider = provider
					.getBottomThumbsBoxProvider();
			// Top Thumb box
			final ThumbsBoxProvider thumbsTopProvider = provider
					.getTopThumbsBoxProvider();
			// Checking top position
			if (!thumbsBottomProvider.shouldShow()
					&& thumbsTopProvider.shouldShow()) {
				// final TranslateAnimation anim = new TranslateAnimation(0, 0,
				// 0,
				// 70);
				// anim.setFillAfter(true);
				final RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) layoutThumbsTop
						.getLayoutParams(); // setVisibility(View.VISIBLE);
				layoutParams.topMargin += 70;
				layoutThumbsTop.setLayoutParams(layoutParams);
			}

			// Bottom box setup
			TextView countText = (TextView) findViewById(R.id.ov_thumb_bot_count);
			ImageView iconView = (ImageView) findViewById(R.id.ov_thumb_bot_icon);
			TextView titleText = (TextView) findViewById(R.id.ov_thumb_bot_title);

			countText.setText(String.valueOf(thumbsBottomProvider.getElements()
					.size()));
			iconView.setImageBitmap(thumbsBottomProvider.getIcon());
			titleText.setText(thumbsBottomProvider.getTitle());

			// Top box setup
			countText = (TextView) findViewById(R.id.ov_thumb_top_count);
			iconView = (ImageView) findViewById(R.id.ov_thumb_top_icon);
			titleText = (TextView) findViewById(R.id.ov_thumb_top_title);

			countText.setText(String.valueOf(thumbsTopProvider.getElements()
					.size()));
			iconView.setImageBitmap(thumbsTopProvider.getIcon());
			titleText.setText(thumbsTopProvider.getTitle());

			likesGallery.setAdapter(new CALObjectThumbAdapter(this,
					thumbsBottomProvider.getElements()));
			topGallery.setAdapter(new CALObjectThumbAdapter(this,
					thumbsTopProvider.getElements()));
			updateVisibility();

		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

	@Override
	public Activity getContext() {
		return this;
	}

	@Override
	public void overviewDataAvailable(CalObject object) {
		progress.setVisibility(View.INVISIBLE);
		setupOverviewData();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("overview", "Result is " + requestCode + " : result code "
				+ resultCode);
		progress.setVisibility(View.VISIBLE);
		final Uri selectedImage = data.getData();
		final File f = imageService.getOrientedImageFileFromUri(this,
				selectedImage);
		// final Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
		imageService.uploadImage(f, provider.getOverviewObject(), user, this);
	}

	@Override
	public void imageUploaded(Image image, CalObject parent) {
		progress.setVisibility(View.INVISIBLE);
		// Adding image to parent
		parent.getImages().add(0, image);
		// Refreshing gallery
		gallery.setAdapter(new ImageAdapter(this, parent));
		// gallery.re
	}

	@Override
	public void imageUploadFailed() {
		progress.setVisibility(View.INVISIBLE);
		final Toast t = Toast.makeText(getBaseContext(),
				getText(R.string.photoUploadFailed), Toast.LENGTH_LONG);
		t.show();
	}

	@Override
	public void liked(CalObject object, int newLikeCount, int newDislikesCount) {
		progress.setVisibility(View.INVISIBLE);
		likesText.setText(String.valueOf(newLikeCount));
		provider.getOverviewObject().setLikeCount(newLikeCount);
	}

	private void updateVisibility() {
		final int v = isHidden ? View.INVISIBLE : View.VISIBLE;
		layoutDescriptionsBox.setVisibility(v);
		layoutLikes.setVisibility(v);
		layoutOverviewTop.setVisibility(v);
		tag1.setVisibility(v);
		tag2.setVisibility(v);
		tag3.setVisibility(v);
		layoutThumbsBottom
				.setVisibility(!isHidden
						&& provider.getBottomThumbsBoxProvider().shouldShow() ? View.VISIBLE
						: View.INVISIBLE);
		layoutThumbsTop
				.setVisibility(!isHidden
						&& provider.getTopThumbsBoxProvider().shouldShow() ? View.VISIBLE
						: View.INVISIBLE);
	}
}
