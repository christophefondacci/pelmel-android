package com.nextep.pelmel.providers.impl;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.R;
import com.nextep.pelmel.helpers.ContextHolder;
import com.nextep.pelmel.model.CalObject;
import com.nextep.pelmel.model.Image;
import com.nextep.pelmel.providers.SnippetInfoProvider;

import java.text.MessageFormat;

/**
 * Created by cfondacci on 21/07/15.
 */
public class ContextSnippetInfoProvider implements SnippetInfoProvider {


    @Override
    public CalObject getItem() {
        return null;
    }

    @Override
    public String getTitle() {
        final CharSequence pattern = PelMelApplication.getInstance().getText(R.string.snippet_context_title);
        return MessageFormat.format(pattern.toString(), ContextHolder.events.size(), ContextHolder.places.size());
    }

    @Override
    public String getSubtitle() {
        final CharSequence pattern = PelMelApplication.getInstance().getText(R.string.snippet_context_subtitle);
        return MessageFormat.format(pattern.toString(), ContextHolder.users.size());
    }

    @Override
    public Bitmap getSubtitleIcon() {
        return BitmapFactory.decodeResource(PelMelApplication.getInstance().getResources(),R.drawable.snp_icon_event);
    }

    @Override
    public Image getSnippetImage() {
        return null;
    }

    @Override
    public int getLikesCount() {
        return 0;
    }

    @Override
    public int getReviewsCount() {
        return 0;
    }

    @Override
    public int getCheckinsCount() {
        return 0;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getItemTypeLabel() {
        return null;
    }

    @Override
    public String getCity() {
        return null;
    }

    @Override
    public String getHoursBadgeTitle() {
        return "";
    }

    @Override
    public String getHoursBadgeSubtitle() {
        return "";
    }

    @Override
    public String getDistanceIntroText() {
        return PelMelApplication.getInstance().getString(R.string.snippet_distance_intro);
    }

    @Override
    public String getDistanceText() {
        String distance = PelMelApplication.getConversionService().getDistanceStringForMiles((double)ContextHolder.radius);
        return distance;
    }
}
