package com.nextep.pelmel.adapters;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.nextep.pelmel.R;
import com.nextep.pelmel.listview.ExpandableListItem;
import com.nextep.pelmel.model.CalObject;

/**
 * Created by cfondacci on 23/07/15.
 */
public class SnippetGalleryAdapter extends BaseAdapter {

    private static final String ROW_GALLERY = "gallery";

    private boolean isOpen = false;
    private Context context;
    private CalObject calObject;
    private LayoutInflater layoutInflater;
    private ExpandableListItem expandableGalleryListItem;
    private ViewPager galleryPager;

    public SnippetGalleryAdapter(Context context, boolean isOpen, CalObject object) {
        this.context = context;
        this.isOpen = isOpen;
        this.calObject = object;
        this.layoutInflater = LayoutInflater.from(context);
        expandableGalleryListItem = new ExpandableListItem(isOpen,0,object);
    }
    @Override
    public int getCount() {
        if(isOpen && !calObject.getImages().isEmpty()) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return expandableGalleryListItem;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_row_gallery, parent, false);

        }
        galleryPager = (ViewPager)convertView.findViewById(R.id.viewPager);
        final ImagePagerAdapter adapter = new ImagePagerAdapter(context,calObject.getImages());
        galleryPager.setAdapter(adapter);
//        ExpandingLayout expandingLayout = (ExpandingLayout)convertView.findViewById(R.id
//                .expanding_layout);
//        expandingLayout.setExpandedHeight(expandableGalleryListItem.getExpandedHeight());
//        expandingLayout.setSizeChangedListener(expandableGalleryListItem);
//
//        if (!expandableGalleryListItem.isExpanded()) {
//            expandingLayout.setVisibility(View.GONE);
//        } else {
//            expandingLayout.setVisibility(View.VISIBLE);
//        }
        expandableGalleryListItem.setContentView(convertView);
        return convertView;
    }

    public void setSnippetOpen(boolean isOpen) {
        this.isOpen = isOpen;
        notifyDataSetChanged();
    }
}
