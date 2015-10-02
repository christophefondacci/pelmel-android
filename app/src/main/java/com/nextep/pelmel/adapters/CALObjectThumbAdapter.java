package com.nextep.pelmel.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.R;
import com.nextep.pelmel.activities.CALObjectGridFragment;
import com.nextep.pelmel.model.CalObject;
import com.nextep.pelmel.model.Image;
import com.nextep.pelmel.model.User;

import java.util.ArrayList;
import java.util.List;

public class CALObjectThumbAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {

	private final Context context;
	private final List<CalObject> calObjects;
	private final int resSize;
	private boolean grid = false;
	private int width;
	private int maxObjects;
	private Resources resources;
	public CALObjectThumbAdapter(Context context, List<? extends CalObject> objects) {
		this(context,objects,R.dimen.thumbs_default_size);
	}
	public CALObjectThumbAdapter(Context context, List<? extends CalObject> objects, int resSize) {
		this.context = context;
		this.calObjects = new ArrayList<>(objects);
		this.resSize = resSize;
		resources = PelMelApplication.getInstance().getResources();
	}

	public void setGrid(boolean grid) {
		this.grid = grid;
		WindowManager wm = (WindowManager)PelMelApplication.getInstance().getSystemService(Context.WINDOW_SERVICE);
		Point p = new Point();
		wm.getDefaultDisplay().getSize(p);
		this.width = p.x-(int)resources.getDimension(R.dimen.snippet_thumbs_hoziontal_margins);
		maxObjects = (int)((float)width / (resources.getDimension(resSize)+10));
	}

	@Override
	public int getCount() {
		if(!grid) {
			return calObjects.size();
		} else {
			return Math.min(calObjects.size(),maxObjects);
		}
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
			viewHolder.moreLabel = (TextView)convertView.findViewById(R.id.moreLabel);
			// Adjusting layout parameters

			final float size =  resources.getDimension(resSize);

			// Sizing image
			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)viewHolder.thumbImageView.getLayoutParams();
			layoutParams.width = (int)size;
			layoutParams.height = (int)size;
			viewHolder.thumbImageView.setLayoutParams(layoutParams);

			// Adjusting radius
			final CalObject obj = (CalObject)getItem(position);
			if((obj instanceof User)&& !(grid && position == maxObjects-1)) {
				viewHolder.thumbImageView.setCornerRadius(size / 2);
			} else {
				viewHolder.thumbImageView.setCornerRadius(0);
			}

			// Sizing label
			viewHolder.title = (TextView) convertView.findViewById(R.id.pseudo);
			layoutParams = (RelativeLayout.LayoutParams)viewHolder.title.getLayoutParams();
			layoutParams.width = (int)size+10;
			viewHolder.title.setLayoutParams(layoutParams);

			// Applying layout
			convertView.requestLayout();

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if(!grid || position < maxObjects-1) {
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

			}
		} else {
			viewHolder.thumbImageView.setImageResource(R.drawable.grid);
//			viewHolder.moreLabel.setText("More");
//			viewHolder.moreLabel.setVisibility(View.VISIBLE);
			viewHolder.title.setText("Grid");
			viewHolder.thumbImageView.setBorderColor(PelMelApplication.getInstance().getResources().getColor(R.color.white));
			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)viewHolder.moreLabel.getLayoutParams();
			layoutParams.topMargin = (int)(resources.getDimension(resSize) / 2.0f) - layoutParams.height/2;
			viewHolder.moreLabel.setLayoutParams(layoutParams);
			viewHolder.thumbImageView.setBorderWidth(0.0f);
			viewHolder.thumbImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
		}

		return convertView;
	}

	private class ViewHolder {
		RoundedImageView thumbImageView;
		TextView title;
		TextView moreLabel;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		final CalObject obj = calObjects.get(position);
		if(!grid || position!=maxObjects-1) {
			PelMelApplication.getSnippetContainerSupport().showSnippetFor(obj, false, false);
		} else {
			CALObjectGridFragment f = new CALObjectGridFragment();
			f.setCalObjects(calObjects);
			PelMelApplication.getSnippetContainerSupport().showSnippetForFragment(f,true,false);
		}
	}
}
