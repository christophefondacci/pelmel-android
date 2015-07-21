package com.nextep.pelmel.activities;

import java.io.File;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.R;
import com.nextep.pelmel.adapters.OldPhotoAdapter;
import com.nextep.pelmel.listeners.ImageRemovalCallback;
import com.nextep.pelmel.listeners.ImageUploadCallback;
import com.nextep.pelmel.listeners.UserListener;
import com.nextep.pelmel.model.CalObject;
import com.nextep.pelmel.model.Image;
import com.nextep.pelmel.model.User;
import com.nextep.pelmel.services.ImageService;

public class PhotosManagerActivity extends MainActionBarActivity implements
		UserListener, ImageUploadCallback, ImageRemovalCallback {

	private ListView photosListView;
	private ImageService imageService;
	private ProgressDialog progressDialog;
	private User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		imageService = PelMelApplication.getImageService();
		setContentView(R.layout.activity_photo_manager);

		// Retrieving main list view
		photosListView = (ListView) findViewById(R.id.photosList);
		PelMelApplication.getUserService().getCurrentUser(this);
	}

	@Override
	public void userInfoAvailable(User user) {
		this.user = user;
		final FragmentManager manager = getSupportFragmentManager();
		// Injecting photos
		final OldPhotoAdapter adapter = new OldPhotoAdapter(getBaseContext(),
				manager, user.getImages(), user, user, this);
		photosListView.setAdapter(adapter);
	}

	@Override
	public void userInfoUnavailable() {
		this.user = null;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("photo", "Result is " + requestCode + " : result code "
				+ resultCode);
		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);
		progressDialog.setMessage(getString(R.string.upload_wait_message));
		progressDialog.setTitle(getString(R.string.waitTitle));
		progressDialog.setIndeterminate(true);
		progressDialog.show();

		// If user cancelled the photo upload, then data will be null
		if (data != null && data.getData() != null) {
			final Uri selectedImage = data.getData();
			final File f = imageService.getOrientedImageFileFromUri(this,
					selectedImage);
			imageService.uploadImage(f, user, user, this);
		}
	}

	@Override
	public void imageUploaded(Image image, CalObject parent) {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		if (user != null) {
			((OldPhotoAdapter) photosListView.getAdapter()).clear();
			user.addImage(image);
			// Injecting photos
			final OldPhotoAdapter adapter = new OldPhotoAdapter(getBaseContext(),
					getSupportFragmentManager(), user.getImages(), user, user,
					this);
			photosListView.setAdapter(adapter);
		}

	}

	@Override
	public void imageUploadFailed() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		final Toast t = Toast.makeText(getBaseContext(),
				getText(R.string.photoUploadFailed), Toast.LENGTH_LONG);
		t.show();
	}

	@Override
	public void imageRemoved(Image image, CalObject fromObject) {
		if (user != null) {
			((OldPhotoAdapter) photosListView.getAdapter()).clear();
			user.removeImage(image);
			// Injecting photos
			final OldPhotoAdapter adapter = new OldPhotoAdapter(getBaseContext(),
					getSupportFragmentManager(), user.getImages(), user, user,
					this);
			photosListView.setAdapter(adapter);
		}
	}
}
