/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nextep.pelmel.listview;

import android.text.Layout;
import android.view.View;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.R;

/**
 * This custom object is used to populate the list adapter. It contains a reference
 * to an image, title, and the extra text to be displayed. Furthermore, it keeps track
 * of the current state (collapsed/expanded) of the corresponding item in the list,
 * as well as store the height of the cell in its collapsed state.
 */
public class ExpandableListItem implements OnSizeChangedListener {

    private boolean mIsExpanded;
    private int mCollapsedHeight;
    private int mExpandedHeight;
    private int mCurrentHeight;
    private Object modelObject;
    private View contentView;
    private Layout contexntViewWrap;

    public ExpandableListItem(boolean isExpanded, int collapsedHeight, Object modelObject) {
        mCollapsedHeight = collapsedHeight;
        mIsExpanded = isExpanded;
        mExpandedHeight = (int)PelMelApplication.getInstance().getResources().getDimension(R.dimen.snippet_gallery_height);
        mCurrentHeight = isExpanded ? mExpandedHeight : mCollapsedHeight;
        this.modelObject = modelObject;
    }

    public Object getModelObject() {
        return modelObject;
    }

    public boolean isExpanded() {
        return mIsExpanded;
    }

    public void setExpanded(boolean isExpanded) {
        mIsExpanded = isExpanded;
    }

//    public String getTitle() {
//        return mTitle;
//    }

//    public int getImgResource() {
//        return mImgResource;
//    }

    public int getCollapsedHeight() {
        return mCollapsedHeight;
    }

    public void setCollapsedHeight(int collapsedHeight) {
        mCollapsedHeight = collapsedHeight;
    }

//    public String getText() {
//        return mText;
//    }

//    public void setText(String text) {
//        mText = text;
//    }

    public int getExpandedHeight() {
        return mExpandedHeight;
    }

    public void setExpandedHeight(int expandedHeight) {
        mExpandedHeight = expandedHeight;
    }

    @Override
    public void onSizeChanged(int newHeight) {
        setExpandedHeight(newHeight);
    }

    public View getContentView() {
        return contentView;
    }

    public int getCurrentHeight() {
        return mCurrentHeight;
    }

    public void setCurrentHeight(int currentHeight) {
        this.mCurrentHeight = currentHeight;
    }

    public void setContentView(View contentView) {
        this.contentView = contentView;
    }
}
