package com.nextep.pelmel.providers.impl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.R;
import com.nextep.pelmel.adapters.CALObjectThumbAdapter;
import com.nextep.pelmel.helpers.ContextHolder;
import com.nextep.pelmel.helpers.Utils;
import com.nextep.pelmel.model.CalObject;
import com.nextep.pelmel.model.Event;
import com.nextep.pelmel.model.Image;
import com.nextep.pelmel.model.User;
import com.nextep.pelmel.providers.CountersProvider;
import com.nextep.pelmel.providers.SnippetInfoProvider;
import com.nextep.pelmel.views.HorizontalListView;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

/**
 * Created by cfondacci on 21/07/15.
 */
public class ContextSnippetInfoProvider implements SnippetInfoProvider, AdapterView.OnItemClickListener {

    private HorizontalListView horizontalListView;

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
        return BitmapFactory.decodeResource(PelMelApplication.getInstance().getResources(), R.drawable.snp_icon_event);
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
        String distance = PelMelApplication.getConversionService().getDistanceStringForMiles((double) ContextHolder.radius);
        return distance;
    }

    @Override
    public List<String> getAddressComponents() {
        return Collections.emptyList();
    }

    @Override
    public List<Event> getEvents() {
        return null;
    }

    @Override
    public boolean hasCustomSnippetView() {
        return true;
    }

    @Override
    public void createCustomSnippetView(Context context, LinearLayout parent) {
        horizontalListView = new HorizontalListView(context,null);
        horizontalListView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        parent.addView(horizontalListView);
        horizontalListView.setOnItemClickListener(this);
    }

    @Override
    public void refreshCustomSnippetView(Context context, LinearLayout parent) {
        WindowManager wm = (WindowManager)PelMelApplication.getInstance().getSystemService(Context.WINDOW_SERVICE);
        Point p = new Point();
        wm.getDefaultDisplay().getSize(p);
        List<CalObject> objects = Utils.sortCalObjectsForDisplay(ContextHolder.users);
        final CALObjectThumbAdapter adapter = new CALObjectThumbAdapter(context,objects);
        adapter.setGrid(true, p.x);
        horizontalListView.setAdapter(adapter);
        horizontalListView.setOnItemClickListener(adapter);
    }

    @Override
    public CountersProvider getCountersProvider() {
        return null;
    }

    @Override
    public int getThumbListsRowCount() {
        return 0;
    }

    @Override
    public List<CalObject> getThumbListObjects(int row) {
        return null;
    }

    @Override
    public String getThumbListSectionTitle(int row) {
        return null;
    }

    @Override
    public Bitmap getThumbListSectionIcon(int row) {
        return null;
    }

    @Override
    public int getHoursColor() {
        return 0;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final CalObject object = (CalObject)horizontalListView.getAdapter().getItem(position);

        PelMelApplication.getSnippetContainerSupport().showSnippetFor(object,true,false);
    }
}
