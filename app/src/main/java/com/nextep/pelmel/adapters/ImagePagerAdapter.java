package com.nextep.pelmel.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.R;
import com.nextep.pelmel.model.Image;

import java.util.List;

/**
 * Created by cfondacci on 23/07/15.
 */
public class ImagePagerAdapter extends PagerAdapter {

    private Context context;
    private List<Image> images;
    private LayoutInflater layoutInflater;

    public ImagePagerAdapter(Context context,List<Image> images) {
        this.context = context;
        this.images = images;
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
    public Object instantiateItem(ViewGroup container, int position) {
        View viewLayout = layoutInflater.inflate(R.layout.fullscreen_image, container,
                false);

        ImageView imgDisplay = (ImageView) viewLayout.findViewById(R.id.galleryImage);
        if(images.size()>position) {
            final Image img = images.get(position);
            PelMelApplication.getImageService().displayImage(img,false,imgDisplay);
        }
        container.addView(viewLayout);
        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }
}
