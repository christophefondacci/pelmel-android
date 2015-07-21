package com.nextep.pelmel.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import com.nextep.pelmel.R;

public class SelectImageDialogFragment extends DialogFragment {

	private static final int ACTIVITY_PICK_PHOTO = 10;
	private static final int ACTIVITY_CAMERA = 100;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		final AlertDialog.Builder builder = new AlertDialog.Builder(
				getActivity());
		builder.setTitle(R.string.selectPhotoTitle).setMessage(
				R.string.selectPhotoMessage);
		builder.setPositiveButton(R.string.selectPhotoCamera,
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						final Intent captureIntent = new Intent(
								MediaStore.ACTION_IMAGE_CAPTURE);
						startActivityForResult(captureIntent, ACTIVITY_CAMERA);
					}
				});

		builder.setNegativeButton(R.string.selectPhotoGallery,
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						final Intent photoIntent = new Intent(
								Intent.ACTION_PICK);
						photoIntent.setType("image/*");
						startActivityForResult(photoIntent, ACTIVITY_PICK_PHOTO);
					}
				});

		return builder.create();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("photo", "Result is " + requestCode + " : result code "
				+ resultCode);
	}
}
