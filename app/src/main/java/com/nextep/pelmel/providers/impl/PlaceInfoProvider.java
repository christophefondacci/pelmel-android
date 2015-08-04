package com.nextep.pelmel.providers.impl;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.LinearLayout;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.R;
import com.nextep.pelmel.activities.Refreshable;
import com.nextep.pelmel.helpers.Strings;
import com.nextep.pelmel.model.Action;
import com.nextep.pelmel.model.CalObject;
import com.nextep.pelmel.model.Event;
import com.nextep.pelmel.model.EventStartState;
import com.nextep.pelmel.model.EventType;
import com.nextep.pelmel.model.Image;
import com.nextep.pelmel.model.Place;
import com.nextep.pelmel.model.RecurringEvent;
import com.nextep.pelmel.providers.CountersProvider;
import com.nextep.pelmel.providers.SnippetInfoProvider;
import com.nextep.pelmel.services.ActionManager;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by cfondacci on 23/07/15.
 */
public class PlaceInfoProvider implements SnippetInfoProvider, CountersProvider {

    private Place place;
    private List<String> addressComponents;
    private RecurringEvent openingsEvent;
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
        for(RecurringEvent event : place.getRecurringEvents()) {
            if(event.getEventType() == EventType.OPENING && (openingsEvent==null || openingsEvent.getStartDate().getTime()>event.getStartDate().getTime())) {
                openingsEvent = event;
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
        return place.getReviewsCount();
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

        final EventStartState state = PelMelApplication.getConversionService().getEventStartState(openingsEvent);
        switch(state) {
            case CURRENT:
                return Strings.getText(R.string.hours_open);
            case SOON:
                return Strings.getText(R.string.hours_closed);
            default:
                return null;
        }
    }

    @Override
    public int getHoursColor() {
        final EventStartState state = PelMelApplication.getConversionService().getEventStartState(openingsEvent);
        final Resources resources = PelMelApplication.getInstance().getResources();
        switch(state) {
            case CURRENT:
                return resources.getColor(R.color.hours_open);
            case SOON:
                return resources.getColor(R.color.hours_closed);
            case PAST:
            default:
                return resources.getColor(R.color.transparent);
        }
    }

    private String getDeltaString(Date date) {
        long delta = (date.getTime()-System.currentTimeMillis())/1000;

        if(delta < 60) {
            delta = 60;
        }
        long value;
        if(delta < 3600 || delta > 999999999) {
            // Display in minutes
            value = delta / 60;
            String minStr = Strings.getText(R.string.time_minutes);
            return value + " " + minStr;
        } else if(delta < 86400) {
            // Display in hours
            value = delta / 3600;
            String hourStr = Strings.getText(R.string.time_hours);
            return value + " " + hourStr;
        } else {
            // Display in days
            value = delta / 86400;
            String dayStr =Strings.getText(R.string.time_days);
            return value + " " + dayStr;
        }
    }
    @Override
    public String getHoursBadgeSubtitle() {
        final EventStartState state = PelMelApplication.getConversionService().getEventStartState(openingsEvent);
        switch(state) {
            case CURRENT: {
                String timeDelta = getDeltaString(openingsEvent.getEndDate());
                String timeTemplate = Strings.getText(R.string.hours_open_leftHours);
                return MessageFormat.format(timeTemplate,timeDelta);
            }
            case SOON: {
                String timeDelta = getDeltaString(openingsEvent.getStartDate());
                String timeTemplate = Strings.getText(R.string.hours_open_in);
                return MessageFormat.format(timeTemplate,timeDelta);
            }

        }
        return null;
    }

    @Override
    public String getDistanceIntroText() {
        return null;
    }

    @Override
    public String getDistanceText() {
        double distance = PelMelApplication.getConversionService().getDistanceTo(place);
        if(distance == 0) {
            return null;
        } else {
            return PelMelApplication.getConversionService().getDistanceStringForMiles(distance);
        }
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

    @Override
    public boolean hasCustomSnippetView() {
        return false;
    }

    @Override
    public void createCustomSnippetView(Context context, LinearLayout parent) {
        throw new UnsupportedOperationException("No custom view for places");
    }

    @Override
    public void refreshCustomSnippetView(Context context, LinearLayout parent) {
        throw new UnsupportedOperationException("No custom view for places");
    }

    @Override
    public CountersProvider getCountersProvider() {
        return this;
    }

    @Override
    public int getThumbListsRowCount() {
        int thumbRows = 0;
        if(place.getLikers()!=null && !place.getLikers().isEmpty()) {
            thumbRows++;
        }
        if(place.getInsiders()!=null && !place.getInsiders().isEmpty()) {
            thumbRows++;
        }
        return thumbRows;
    }

    @Override
    public List<CalObject> getThumbListObjects(int row) {
        if(row == 0 && place.getLikers() != null && !place.getLikers().isEmpty()) {
            return (List)place.getLikers();
        } else {
            return (List)place.getInsiders();
        }
    }

    @Override
    public String getThumbListSectionTitle(int row) {
        if(row == 0 && place.getLikers() != null && !place.getLikers().isEmpty()) {
            return Strings.getCountedText(R.string.thumbs_section_like_singular,R.string.thumbs_section_like,place.getLikeCount()).toString();
        } else {
            return Strings.getCountedText(R.string.thumbs_section_checkedin_singular,R.string.thumbs_section_checkedin,place.getInsidersCount()).toString();
        }
    }

    @Override
    public Bitmap getThumbListSectionIcon(int row) {
        if(row == 0 && place.getLikers() != null && !place.getLikers().isEmpty()) {
            return BitmapFactory.decodeResource(PelMelApplication.getInstance().getResources(), R.drawable.snp_icon_like_white);
        } else {
            return BitmapFactory.decodeResource(PelMelApplication.getInstance().getResources(), R.drawable.ovv_icon_check_white);
        }
    }

    private boolean isCheckinEnabled(CalObject object) {
        return PelMelApplication.getLocalizationService().isCheckinEnabled(object);
    }
    private boolean isCheckedIn() {
        return PelMelApplication.getUserService().isCheckedInAt(place);
    }
    @Override
    public String getCounterLabelAtIndex(int index) {
        switch(index) {
            case COUNTER_LIKE:
                return Strings.getCountedText(R.string.counter_likes_singular,R.string.counter_likes,place.getLikeCount()).toString();
            case COUNTER_CHECKIN:
                return Strings.getCountedText(R.string.counter_arehere_singular,R.string.counter_arehere,place.getInsidersCount()).toString();
            case COUNTER_CHAT:
                return Strings.getCountedText(R.string.counter_comments_singular,R.string.counter_comments,place.getReviewsCount()).toString();
        }
        return null;
    }

    @Override
    public String getCounterActionLabelAtIndex(int index) {
        int res = 0;
        switch(index) {
            case COUNTER_LIKE:
                if(place.isLiked()) {
                    res = R.string.action_unlike;
                } else {
                    res = R.string.action_like;
                }
                break;
            case COUNTER_CHECKIN:
                if(isCheckedIn()) {
                    res = R.string.action_checkout;
                } else {
                    res = R.string.action_checkin;
                }
                break;
            case COUNTER_CHAT:
                res = R.string.action_comment;
        }
        if(res!=0) {
            return Strings.getText(res);
        }
        return null;
    }

    @Override
    public boolean isCounterSelectedAtIndex(int index) {
        switch(index) {
            case COUNTER_LIKE:
                return place.isLiked();
            case COUNTER_CHECKIN:
                return isCheckedIn();
            default:
                return false;
        }
    }

    @Override
    public void executeCounterActionAtIndex(final Context context, final Refreshable refreshable, int index) {
        ActionManager mgr = PelMelApplication.getActionManager();
        final boolean selected = isCounterSelectedAtIndex(index);
        switch(index) {
            case COUNTER_LIKE:
                mgr.executeAction(selected ? Action.UNLIKE : Action.LIKE, place, new ActionManager.ActionCallback() {
                    @Override
                    public void actionCompleted(boolean isSucess, Object result) {
                        if(!selected) {
                            PelMelApplication.getUiService().showInfoMessage(context, R.string.alert_like_success_title, R.string.alert_like_place_success);
                        } else {
                            PelMelApplication.getUiService().showInfoMessage(context, R.string.alert_unlike_success_title, R.string.alert_unlike_place_success);
                        }
                        refreshable.updateData();

                    }
                });
                break;
            case COUNTER_CHECKIN:
                if (isCheckedIn()) {
                    mgr.executeAction(Action.CHECKOUT, place, new ActionManager.ActionCallback() {
                        @Override
                        public void actionCompleted(boolean isSucess, Object result) {
                            PelMelApplication.getUiService().showInfoMessage(context, R.string.alert_checkout_success_title, R.string.alert_checkout_success,place.getName());
                            refreshable.updateData();
                        }
                    });
                } else {
                    mgr.executeAction(Action.CHECKIN, place, new ActionManager.ActionCallback() {
                        @Override
                        public void actionCompleted(boolean isSucess, Object result) {
                            PelMelApplication.getUiService().showInfoMessage(context, R.string.alert_checkin_success_title, R.string.alert_checkin_success);
                            refreshable.updateData();
                        }
                    });
                }
        }
    }
    @Override
    public CalObject getCounterObject() {
        return null;
    }

    @Override
    public Bitmap getCounterImageAtIndex(int index) {
        switch(index) {
            case COUNTER_LIKE:
                return BitmapFactory.decodeResource(PelMelApplication.getInstance().getResources(),R.drawable.snp_icon_like_white);
            case COUNTER_CHECKIN:
                return BitmapFactory.decodeResource(PelMelApplication.getInstance().getResources(),R.drawable.ovv_icon_check_white);
            case COUNTER_CHAT:
                return BitmapFactory.decodeResource(PelMelApplication.getInstance().getResources(),R.drawable.snp_icon_chat);
        }
        return null;
    }

    @Override
    public boolean hasCounter(int index) {
        if(index == COUNTER_CHECKIN) {
            return isCheckinEnabled(place) || isCheckedIn();
        }
        return true;
    }
}
