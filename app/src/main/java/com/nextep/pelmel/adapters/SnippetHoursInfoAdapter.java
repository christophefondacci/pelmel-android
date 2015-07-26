package com.nextep.pelmel.adapters;

import android.content.Context;
import android.graphics.Bitmap;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.model.RecurringEvent;
import com.nextep.pelmel.providers.SnippetInfoProvider;
import com.nextep.pelmel.services.ConversionService;

import java.util.List;

/**
 * Created by cfondacci on 26/07/15.
 */
public class SnippetHoursInfoAdapter extends BaseSnippetInfoAdapter {

    private SnippetInfoProvider infoProvider;
    private List<RecurringEvent> events;
    public SnippetHoursInfoAdapter(Context context, List<RecurringEvent> events) {
        super(context);
        this.events = events;
    }
    @Override
    protected String getInfoText(int position) {
        ConversionService conversionService = PelMelApplication.getConversionService();
        final String hoursLabel = conversionService.getRecurringEventLabel(events.get(position));
        return hoursLabel;
    }

    @Override
    protected Bitmap getInfoImage(int position) {
        return null;
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Object getItem(int position) {
        return events.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
