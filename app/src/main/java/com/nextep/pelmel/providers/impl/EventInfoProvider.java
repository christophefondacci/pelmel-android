package com.nextep.pelmel.providers.impl;

import android.content.Context;
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
import com.nextep.pelmel.model.Image;
import com.nextep.pelmel.providers.CountersProvider;
import com.nextep.pelmel.providers.SnippetInfoProvider;
import com.nextep.pelmel.services.ActionManager;
import com.nextep.pelmel.services.ConversionService;

import java.util.List;

/**
 * Created by cfondacci on 06/08/15.
 */
public class EventInfoProvider implements SnippetInfoProvider, CountersProvider {

    private Event event;
    public EventInfoProvider(Event event) {
        this.event = event;
    }
    @Override
    public CalObject getItem() {
        return event;
    }

    @Override
    public String getTitle() {
        return Strings.getName(event);
    }

    @Override
    public String getSubtitle() {
        return Strings.getEventDate(event, true) + " - " + Strings.getEventDate(event,false);
    }

    @Override
    public Bitmap getSubtitleIcon() {
        return BitmapFactory.decodeResource(PelMelApplication.getInstance().getResources(), R.drawable.snp_icon_ticket);
    }

    @Override
    public Image getSnippetImage() {
        return event.getThumb();
    }

    @Override
    public int getLikesCount() {
        return event.getLikeCount();
    }

    @Override
    public int getReviewsCount() {
        return event.getReviewsCount();
    }

    @Override
    public int getCheckinsCount() {
        if(event.getPlace() != null) {
            return event.getPlace().getInsidersCount();
        } else {
            return 0;
        }
    }

    @Override
    public String getDescription() {
        return event.getDescription();
    }

    @Override
    public String getItemTypeLabel() {
        return null;
    }

    @Override
    public String getCity() {
        if(event.getPlace()!=null) {
            return event.getPlace().getCityName();
        } else {
            return null;
        }
    }

    @Override
    public String getHoursBadgeSubtitle() {
        return null;
    }

    @Override
    public String getHoursBadgeTitle() {
        return null;
    }

    @Override
    public int getHoursColor() {
        return 0;
    }

    @Override
    public String getDistanceIntroText() {
        return null;
    }

    @Override
    public String getDistanceText() {
        if(event.getPlace() != null) {
            final ConversionService conversionService = PelMelApplication.getConversionService();
            final double distance = conversionService.getDistanceTo(event.getPlace());
            return conversionService.getDistanceStringForMiles(distance);
        } else {
            return null;
        }
    }

    @Override
    public List<String> getAddressComponents() {
        if(event.getPlace() !=null) {
            PlaceInfoProvider placeProvider = new PlaceInfoProvider(event.getPlace());
            return placeProvider.getAddressComponents();
        }
        return null;
    }

    @Override
    public List<Event> getEvents() {
        return null;
    }

    @Override
    public boolean hasCustomSnippetView() {
        return false;
    }

    @Override
    public void createCustomSnippetView(Context context, LinearLayout parent) {

    }

    @Override
    public void refreshCustomSnippetView(Context context, LinearLayout parent) {

    }

    @Override
    public CountersProvider getCountersProvider() {
        return this;
    }

    @Override
    public int getThumbListsRowCount() {
        return event.getComersCount()>0 ?  1 : 0;
    }

    @Override
    public List<CalObject> getThumbListObjects(int row) {
        return (List)event.getComers();
    }

    @Override
    public String getThumbListSectionTitle(int row) {
        return Strings.getCountedText(R.string.thumbs_section_attend_singular,R.string.thumbs_section_attend,event.getComersCount()).toString();
    }

    @Override
    public Bitmap getThumbListSectionIcon(int row) {
        return BitmapFactory.decodeResource(PelMelApplication.getInstance().getResources(), R.drawable.snp_icon_event);
    }

    @Override
    public String getCounterLabelAtIndex(int index) {
        switch(index) {
            case COUNTER_LIKE:
                return Strings.getCountedText(R.string.counter_attend_singular,R.string.counter_attend,event.getComersCount()).toString();
            case COUNTER_CHECKIN:
                if(event.getPlace() != null) {
                    return Strings.getCountedText(R.string.counter_arehere_singular, R.string.counter_arehere, event.getPlace().getInsidersCount()).toString();
                } else {
                    return null;
                }
            case COUNTER_CHAT:
                return Strings.getCountedText(R.string.counter_comments_singular,R.string.counter_comments,event.getReviewsCount()).toString();
        }
        return null;
    }

    @Override
    public boolean hasCounter(int index) {
        if(index == COUNTER_CHECKIN) {
            return event.getStartDate().getTime()<System.currentTimeMillis() && event.getEndDate().getTime()> System.currentTimeMillis();
        } else {
            return true;
        }
    }
    private boolean isCheckedIn() {
        if(event.getPlace() != null) {
            return PelMelApplication.getUserService().isCheckedInAt(event.getPlace());
        } else {
            return false;
        }
    }
    @Override
    public String getCounterActionLabelAtIndex(int index) {
        int res = 0;
        switch(index) {
            case COUNTER_LIKE:
                if(event.isLiked()) {
                    res = R.string.action_unattend;
                } else {
                    res = R.string.action_attend;
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
                return event.isLiked();
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
                mgr.executeAction(selected ? Action.UNATTEND : Action.ATTEND, event, new ActionManager.ActionCallback() {
                    @Override
                    public void actionCompleted(boolean isSucess, Object result) {
                        if(!selected) {
                            PelMelApplication.getUiService().showInfoMessage(context, R.string.alert_attend_success_title, R.string.alert_attend_success);
                        } else {
                            PelMelApplication.getUiService().showInfoMessage(context, R.string.alert_unattend_success_title, R.string.alert_unattend_success);
                        }
                        refreshable.updateData();

                    }
                });
                break;
            case COUNTER_CHECKIN:
                if (isCheckedIn()) {
                    mgr.executeAction(Action.CHECKOUT, event.getPlace(), new ActionManager.ActionCallback() {
                        @Override
                        public void actionCompleted(boolean isSucess, Object result) {
                            PelMelApplication.getUiService().showInfoMessage(context, R.string.alert_checkout_success_title, R.string.alert_checkout_success,event.getPlace().getName());
                            refreshable.updateData();
                        }
                    });
                } else {
                    mgr.executeAction(Action.CHECKIN, event.getPlace(), new ActionManager.ActionCallback() {
                        @Override
                        public void actionCompleted(boolean isSucess, Object result) {
                            PelMelApplication.getUiService().showInfoMessage(context, R.string.alert_checkin_success_title, R.string.alert_checkin_success);
                            refreshable.updateData();
                        }
                    });
                }
                break;
            case COUNTER_CHAT:
                mgr.executeAction(Action.CHAT,event);
                break;
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
                return BitmapFactory.decodeResource(PelMelApplication.getInstance().getResources(),R.drawable.snp_icon_event);
            case COUNTER_CHECKIN:
                return BitmapFactory.decodeResource(PelMelApplication.getInstance().getResources(),R.drawable.ovv_icon_check_white);
            case COUNTER_CHAT:
                return BitmapFactory.decodeResource(PelMelApplication.getInstance().getResources(),R.drawable.snp_icon_chat);
        }
        return null;
    }
}
