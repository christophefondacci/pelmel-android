package com.nextep.pelmel.services.impl;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.nextep.json.model.impl.JsonMedia;
import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.R;
import com.nextep.pelmel.listeners.ImageRemovalCallback;
import com.nextep.pelmel.listeners.ImageUploadCallback;
import com.nextep.pelmel.model.CalObject;
import com.nextep.pelmel.model.Image;
import com.nextep.pelmel.model.User;
import com.nextep.pelmel.services.ImageService;
import com.nextep.pelmel.services.WebService;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageServiceImpl implements ImageService {

	private static final String TAG_IMAGE_SERVICE = "IMAGE_SERVICE";
	private static final int MAX_IMAGE_DIMENSION = 1000;
	private final Map<String, String> imageViews = Collections
			.synchronizedMap(new WeakHashMap<String, String>());
	private final ExecutorService executorService;
	private final Map<Integer, BitmapDescriptor> descriptorsMap = new HashMap<Integer, BitmapDescriptor>();
	final int stub_id = R.drawable.no_photo_big;
	private final ImageLoader imageLoader;

	public ImageServiceImpl(Context context) {
		executorService = Executors.newFixedThreadPool(5);
		imageLoader = ImageLoader.getInstance();
	}

	@Override
	public void displayImage(final Image image, final boolean isThumb,
			ImageView imageView) {
		// Immediately displaying thumb if available
		if (!isThumb && image.isThumbLoaded()) {
			imageView.setImageBitmap(image.getThumb());
		}

		// And loading full image
		imageLoader.displayImage(
				isThumb ? image.getThumbUrl() : image.getUrl(), imageView,
				new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String arg0, View arg1) {
					}

					@Override
					public void onLoadingFailed(String arg0, View arg1,
							FailReason arg2) {
					}

					@Override
					public void onLoadingComplete(String arg0, View arg1,
							Bitmap arg2) {
						if (isThumb) {
							image.setThumbLoaded(true);
							image.setThumb(arg2);
						}
					}

					@Override
					public void onLoadingCancelled(String arg0, View arg1) {
					}
				});
		// final Bitmap bitmap = isThumb ? image.getThumb() :
		// image.getFullImage();
		//
		// Log.d(TAG_IMAGE_SERVICE, "Requested ImageView=" + imageView +
		// " / URL="
		// + image.getThumbUrl());
		// if (bitmap != null) {
		// imageView.setImageBitmap(bitmap);
		// } else {
		// imageViews.put(image.getKey(), isThumb ? image.getThumbUrl()
		// : image.getUrl());
		// queuePhoto(image, isThumb, imageView);
		// imageView.setImageResource(stub_id);
		// }
	}

	private void queuePhoto(Image image, boolean isThumb, ImageView imageView) {
		final PhotoToLoad p = new PhotoToLoad(image, isThumb, imageView);
		executorService.submit(new PhotosLoader(p));
	}

	private Bitmap getBitmap(String url) {
		Bitmap bitmap = null;
		try {
			final URL imageUrl = new URL(url);
			final HttpURLConnection conn = (HttpURLConnection) imageUrl
					.openConnection();
			conn.setInstanceFollowRedirects(true);
			final InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		return bitmap;
	}

	private Bitmap decodeStream(InputStream is) {
		// decode image size
		final BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(is, null, o);

		// Find the correct scale value. It should be the power of 2.
		final int REQUIRED_SIZE = 70;
		int width_tmp = o.outWidth, height_tmp = o.outHeight;
		int scale = 1;
		while (true) {
			if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
				break;
			width_tmp /= 2;
			height_tmp /= 2;
			scale *= 2;
		}

		// decode with inSampleSize
		final BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = scale;
		return BitmapFactory.decodeStream(is, null, o2);
	}

	// Task for the queue
	private class PhotoToLoad {
		public Image image;
		public boolean isThumb;
		public ImageView imageView;

		public PhotoToLoad(Image image, boolean isThumb, ImageView i) {
			this.image = image;
			this.isThumb = isThumb;
			imageView = i;
		}
	}

	/**
	 * The task that downloads photos from the server
	 *
	 * @author cfondacci
	 *
	 */
	class PhotosLoader implements Runnable {
		PhotoToLoad photoToLoad;

		PhotosLoader(PhotoToLoad photoToLoad) {
			this.photoToLoad = photoToLoad;
		}

		@Override
		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			// Getting the URL (thumb or full image)
			final String url = photoToLoad.isThumb ? photoToLoad.image
					.getThumbUrl() : photoToLoad.image.getUrl();
			// Getting bitmap from URL
			final Bitmap bmp = getBitmap(url);

			Log.d(TAG_IMAGE_SERVICE, "Got bitmap for ImageView="
					+ photoToLoad.imageView + " / URL=" + url);
			// Storing bitmap back in image
			if (photoToLoad.isThumb) {
				photoToLoad.image.setThumb(bmp);
			} else {
				photoToLoad.image.setFullImage(bmp);
			}
			if (imageViewReused(photoToLoad))
				return;
			final BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
			final Activity a = (Activity) photoToLoad.imageView.getContext();
			a.runOnUiThread(bd);
		}
	}

	boolean imageViewReused(PhotoToLoad photoToLoad) {
		final String tag = imageViews.get(photoToLoad.image.getKey());
		if (tag == null
				|| !tag.equals(photoToLoad.isThumb ? photoToLoad.image
						.getThumbUrl() : photoToLoad.image.getUrl())) {
			return true;
		}
		return false;
	}

	// Used to display bitmap in the UI thread
	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		PhotoToLoad photoToLoad;

		public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
			bitmap = b;
			photoToLoad = p;
		}

		@Override
		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			if (bitmap != null)
				photoToLoad.imageView.setImageBitmap(bitmap);
			else
				photoToLoad.imageView.setImageResource(stub_id);
		}
	}

	@Override
	public void clearCache() {
	}

	@Override
	public void uploadImage(File imageFile, CalObject parent, User user,
			ImageUploadCallback callback) {
		final ImageUploader uploader = new ImageUploader(imageFile, parent,
				user, callback);
		executorService.execute(uploader);
	}

	private class ImageUploader implements Runnable {
		private final File imageFile;
		private final CalObject parent;
		private final User user;
		private final ImageUploadCallback callback;

		public ImageUploader(File imageFile, CalObject parent, User user,
				ImageUploadCallback callback) {
			this.imageFile = imageFile;
			this.parent = parent;
			this.user = user;
			this.callback = callback;
		}

		@Override
		public void run() {
			final HttpClient http = new DefaultHttpClient();
			final HttpPost post = new HttpPost(WebService.BASE_URL
					+ "/mobileAddMedia");

			final MultipartEntity multipart = new MultipartEntity();
			try {
				multipart.addPart("parentKey", new StringBody(parent.getKey()));
				multipart.addPart("nxtpUserToken",
						new StringBody(user.getToken()));
				final FileBody fileBody = new FileBody(imageFile);
				// ByteArrayOutputStream stream = new ByteArrayOutputStream();
				// bitmap.compress(CompressFormat.PNG, 0, stream);
				multipart.addPart("media", fileBody);
				// new ByteArrayBody(stream.toByteArray(), "image/png",
				// "image.png"));
				post.setEntity(multipart);
				final HttpResponse response = http.execute(post);
				final HttpEntity entity = response.getEntity();
				if (entity != null) {
					final InputStream is = entity.getContent();
					final InputStreamReader reader = new InputStreamReader(is);
					final Gson gson = new Gson();
					try {
						final JsonMedia media = gson.fromJson(reader,
								new TypeToken<JsonMedia>() {
								}.getType());
						final Image img = PelMelApplication.getDataService()
								.getImageFromJson(media);
						// Notifying callback
						PelMelApplication.runOnMainThread(new Runnable() {

							@Override
							public void run() {
								callback.imageUploaded(img, parent);
							}
						});
					} catch (final JsonSyntaxException e) {
						Log.e(TAG_IMAGE_SERVICE,
								"Unable to parse JSON result from media upload: "
										+ e.getMessage());
					}

				}
			} catch (final UnsupportedEncodingException e) {
				Log.e(TAG_IMAGE_SERVICE,
						"Error uploading image : " + e.getMessage());
			} catch (final ClientProtocolException e) {
				Log.e(TAG_IMAGE_SERVICE,
						"Error uploading image : " + e.getMessage());
			} catch (final IOException e) {
				Log.e(TAG_IMAGE_SERVICE,
						"Error uploading image : " + e.getMessage());
			}
			PelMelApplication.runOnMainThread(new Runnable() {

				@Override
				public void run() {
					callback.imageUploadFailed();
				}
			});
		}
	}

	@Override
	public void removeImage(final Image image, final CalObject parent,
			final User user, final ImageRemovalCallback callback) {
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				final HttpClient http = new DefaultHttpClient();
				final HttpPost post = new HttpPost(WebService.BASE_URL
						+ "/mobileDeleteMedia");
				final MultipartEntity multipart = new MultipartEntity();
				try {
					multipart.addPart("id", new StringBody(image.getKey()
							.toString()));
					multipart.addPart("confirmed", new StringBody("true"));
					multipart.addPart("nxtpUserToken",
							new StringBody(user.getToken()));
					post.setEntity(multipart);
					HttpResponse response;
					response = http.execute(post);
					final HttpEntity entity = response.getEntity();
					if (entity != null) {
						PelMelApplication.runOnMainThread(new Runnable() {

							@Override
							public void run() {
								callback.imageRemoved(image, parent);
							}
						});
					}
				} catch (final UnsupportedEncodingException e) {
					Log.e(TAG_IMAGE_SERVICE,
							"Error removing image : " + e.getMessage());
				} catch (final ClientProtocolException e) {
					Log.e(TAG_IMAGE_SERVICE,
							"Error removing image : " + e.getMessage());
				} catch (final IOException e) {
					Log.e(TAG_IMAGE_SERVICE,
							"Error removing image : " + e.getMessage());
				}
			}
		});
	}

	private int getOrientation(Context context, Uri photoUri) {
		/* it's on the external media. */
		final Cursor cursor = context.getContentResolver().query(photoUri,
				new String[] { MediaStore.Images.ImageColumns.ORIENTATION },
				null, null, null);

		if (cursor.getCount() != 1) {
			return -1;
		}

		cursor.moveToFirst();
		return cursor.getInt(0);
	}

	@Override
	public File getOrientedImageFileFromUri(Context context, Uri imageUri) {
		try {
			InputStream is = context.getContentResolver().openInputStream(
					imageUri);
			final BitmapFactory.Options dbo = new BitmapFactory.Options();
			dbo.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(is, null, dbo);
			is.close();

			int rotatedWidth, rotatedHeight;
			final int orientation = getOrientation(context, imageUri);

			if (orientation == 90 || orientation == 270) {
				rotatedWidth = dbo.outHeight;
				rotatedHeight = dbo.outWidth;
			} else {
				rotatedWidth = dbo.outWidth;
				rotatedHeight = dbo.outHeight;
			}

			Bitmap srcBitmap;
			is = context.getContentResolver().openInputStream(imageUri);
			if (rotatedWidth > MAX_IMAGE_DIMENSION
					|| rotatedHeight > MAX_IMAGE_DIMENSION) {
				final float widthRatio = rotatedWidth
						/ ((float) MAX_IMAGE_DIMENSION);
				final float heightRatio = rotatedHeight
						/ ((float) MAX_IMAGE_DIMENSION);
				final float maxRatio = Math.max(widthRatio, heightRatio);

				// Create the bitmap from file
				final BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = (int) maxRatio;
				srcBitmap = BitmapFactory.decodeStream(is, null, options);
			} else {
				srcBitmap = BitmapFactory.decodeStream(is);
			}
			is.close();

			/*
			 * if the orientation is not 0 (or -1, which means we don't know),
			 * we have to do a rotation.
			 */
			if (orientation > 0) {
				final Matrix matrix = new Matrix();
				matrix.postRotate(orientation);

				srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0,
						srcBitmap.getWidth(), srcBitmap.getHeight(), matrix,
						true);
			}

			// Dumping to file
			final File f = File.createTempFile(
					"pelmelimage." + System.currentTimeMillis(), "png",
					context.getCacheDir());

			// Streaming into temp file (don't want to mess with the crappy
			// android APIs to convert URI to file with many deprecated methods)
			OutputStream os = null;
			try {
				os = new FileOutputStream(f);
				srcBitmap.compress(CompressFormat.PNG, 0, os);
			} finally {
				os.close();
			}
			return f;
		} catch (final FileNotFoundException e) {
			Log.e(TAG_IMAGE_SERVICE, "File not found while converting image: "
					+ e.getMessage());
		} catch (final IOException e) {
			Log.e(TAG_IMAGE_SERVICE,
					"IOException while converting image: " + e.getMessage());
		}

		return null;
	}
}
