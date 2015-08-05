package com.nextep.pelmel.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nextep.pelmel.R;

/**
 * Created by cfondacci on 04/08/15.
 */
public class ProfileSectionedAdapter extends SectionedAdapter {
    public static final String SECTION_HEADER = "header";
    public static final String SECTION_PHOTOS = "photos";
    public static final String SECTION_DESCRIPTIONS = "description";
    public static final String SECTION_TAGS= "tags";

    private LayoutInflater layoutInflater;
    public ProfileSectionedAdapter(Context context) {
        super(context);
        layoutInflater = LayoutInflater.from(context);
    }
    @Override
    protected View getHeaderView(String caption, int index, View convertView, ViewGroup parent) {
        switch(caption) {
            case SECTION_PHOTOS:
                return getSectionTitleConvertView(convertView,parent, R.string.section_title_photos);
            case SECTION_DESCRIPTIONS:
                return getSectionTitleConvertView(convertView,parent, R.string.section_title_descriptions);
            case SECTION_TAGS:
                return getSectionTitleConvertView(convertView,parent, R.string.section_title_tags);
            default:
                break;
        }
        return layoutInflater.inflate(R.layout.list_row_empty,parent,false);
    }
}
