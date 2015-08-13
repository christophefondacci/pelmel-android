package com.nextep.pelmel.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.PelMelConstants;
import com.nextep.pelmel.R;
import com.nextep.pelmel.activities.GalleryActivity;
import com.nextep.pelmel.dialogs.SelectImageDialogFragment;
import com.nextep.pelmel.model.CalObject;
import com.nextep.pelmel.model.Image;
import com.nextep.pelmel.model.User;

import java.util.List;

/**
 * Created by cfondacci on 23/07/15.
 */
public class ImagePagerAdapter extends PagerAdapter {

    private Context context;
    private CalObject calObject;
    private List<Image> images;
    private LayoutInflater layoutInflater;
    private boolean clickEnabled;

    public ImagePagerAdapter(Context context,CalObject calObject, boolean clickEnabled) {
        this.context = context;
        this.calObject = calObject;
        this.images = calObject.getImages();
        this.clickEnabled = clickEnabled;
        layoutInflater =(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return Math.max(images.size(), 1);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View viewLayout = layoutInflater.inflate(R.layout.fullscreen_image, container,
                false);

        ImageView imgDisplay = (ImageView) viewLayout.findViewById(R.id.galleryImage);
        if(images.size()>position) {
            final Image img = images.get(position);
            PelMelApplication.getImageService().displayImage(img,false,imgDisplay);
        } else {
            imgDisplay.setImageBitmap(PelMelApplication.getUiService().getNoPhotoFor(calObject,false));
        }
        container.addView(viewLayout);

        if(clickEnabled && !(calObject instanceof User)) {
            imgDisplay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(position < images.size()) {
                        final Intent intent = new Intent(context, GalleryActivity.class);
                        intent.putExtra(PelMelConstants.INTENT_PARAM_INDEX, position);
                        intent.putExtra(PelMelConstants.INTENT_PARAM_CAL_KEY, calObject.getKey());
                        context.startActivity(intent);
                    } else {
                        PelMelApplication.setOverviewObject(calObject);
                        final SelectImageDialogFragment selectDialog = new SelectImageDialogFragment();
                        selectDialog.show(((FragmentActivity)context).getSupportFragmentManager(), "PHOTO");
                    }
//                    GalleryActivity activity = new GalleryActivity();
//                    activity.setImages(images.get(position), images);
//                    PelMelApplication.getSnippetContainerSupport().showSnippetForFragment(activity, true, false);
                }
            });
        }
        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }
}
