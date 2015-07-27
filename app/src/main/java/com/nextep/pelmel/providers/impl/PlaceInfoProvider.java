package com.nextep.pelmel.providers.impl;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.model.CalObject;
import com.nextep.pelmel.model.Event;
import com.nextep.pelmel.model.EventType;
import com.nextep.pelmel.model.Image;
import com.nextep.pelmel.model.Place;
import com.nextep.pelmel.model.RecurringEvent;
import com.nextep.pelmel.providers.SnippetInfoProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by cfondacci on 23/07/15.
 */
public class PlaceInfoProvider implements SnippetInfoProvider {

    private Place place;
    private List<String> addressComponents;
    public PlaceInfoProvider(Place place) {
        this.place = place;
        configureAddress();
    }
    private void configureAddress() {
        addressComponents = new ArrayList<>();
        if(place.getAddress() != null) {
            // Splitting address by comma
            String[] components = place.getAddress().split(",");
            String currentComponent = "";
            for(String component : components) {
                currentComponent = currentComponent + component;
                if(currentComponent.length()>=5) {
                    addressComponents.add(currentComponent);
                    currentComponent = "";
                }
            }
        }
    }
    @Override
    public CalObject getItem() {
        return place;
    }

    @Override
    public String getTitle() {
        return place.getName();
    }

    @Override
    public String getSubtitle() {
        final int placeTypeLabelResource = PelMelApplication.getUiService()
                .getLabelForPlaceType(place.getType());
        final String placeTypeLabel = PelMelApplication.getInstance()
                .getString(placeTypeLabelResource);
        return placeTypeLabel;
    }

    @Override
    public Bitmap getSubtitleIcon() {
        int res = PelMelApplication.getUiService().getIconForPlaceType(place.getType());
        return BitmapFactory.decodeResource(PelMelApplication.getInstance().getResources(), res);
    }

    @Override
    public Image getSnippetImage() {
        return place.getThumb();
    }

    @Override
    public int getLikesCount() {
        return place.getLikeCount();
    }

    @Override
    public int getReviewsCount() {
        return 0;
    }

    @Override
    public int getCheckinsCount() {
        return place.getInsidersCount();
    }

    @Override
    public String getDescription() {
        return place.getDescription();
    }

    @Override
    public String getItemTypeLabel() {
        return null;
    }

    @Override
    public String getCity() {
        return place.getCityName();
    }

    @Override
    public String getHoursBadgeTitle() {
        return null;
    }

    @Override
    public String getHoursBadgeSubtitle() {
        return null;
    }

    @Override
    public String getDistanceIntroText() {
        return null;
    }

    @Override
    public String getDistanceText() {
        double distance = PelMelApplication.getConversionService().getDistanceTo(place);
        return PelMelApplication.getConversionService().getDistanceStringForMiles(distance);
    }

    @Override
    public List<String> getAddressComponents() {
        return addressComponents;
    }

    @Override
    public List<Event> getEvents() {
        List<Event> events = new ArrayList<>();

        // Adding Theme nights events
        for (RecurringEvent e : place.getRecurringEvents()) {
            if(e.getEventType() == EventType.THEME) {
                if(e.getStartDate() !=null) {
                    events.add(e);
                }
            }
        }
        events.addAll(place.getEvents());
        Collections.sort(events, new Comparator<Event>() {
            @Override
            public int compare(Event lhs, Event rhs) {
                return lhs.getStartDate().compareTo(rhs.getStartDate());
            }
        });
        return events;
    }
}
