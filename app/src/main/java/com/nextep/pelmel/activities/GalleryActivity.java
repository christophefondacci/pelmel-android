package com.nextep.pelmel.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.PelMelConstants;
import com.nextep.pelmel.R;
import com.nextep.pelmel.adapters.ImagePagerAdapter;
import com.nextep.pelmel.listeners.OverviewListener;
import com.nextep.pelmel.model.CalObject;
import com.nextep.pelmel.model.Image;

import java.util.List;

/**
 * Created by cfondacci on 05/08/15.
 */
public class GalleryActivity extends Activity {

    private ViewPager galleryPager;
    private List<Image> images;
    private int index = 0;

    public void setCalObject(CalObject object) {
        this.images= object.getImages();
    }

    public void setImages(Image current, List<Image> allImages) {
        this.images = allImages;
        this.index = this.images.indexOf(current);
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        final View view = inflater.inflate(R.layout.activity_gallery,container,false);
//        galleryPager = (ViewPager)view.findViewById(R.id.viewPager);
//        final ImagePagerAdapter adapter = new ImagePagerAdapter(this,images,false);
//        galleryPager.setAdapter(adapter);
//        if(index >0) {
//            galleryPager.setCurrentItem(index);
//        }
//        return view;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        galleryPager = (ViewPager)findViewById(R.id.viewPager);

        final int index = getIntent().getIntExtra(PelMelConstants.INTENT_PARAM_INDEX, 0);
        final String objKey = getIntent().getStringExtra(PelMelConstants.INTENT_PARAM_CAL_KEY);
        final CalObject obj = PelMelApplication.getDataService().getCalObject(objKey, new OverviewListener() {
            @Override
            public Activity getContext() {
                return GalleryActivity.this;
            }

            @Override
            public void overviewDataAvailable(CalObject object) {
                refreshData(index, object);
            }
        });
        refreshData(index, obj);
    }

    private void refreshData(int index, CalObject object) {
        final ImagePagerAdapter adapter = new ImagePagerAdapter(this,object,false);
        galleryPager.setAdapter(adapter);
        if(index >0) {
            galleryPager.setCurrentItem(index);
        }
    }
}
