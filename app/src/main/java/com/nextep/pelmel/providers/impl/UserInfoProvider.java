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
import com.nextep.pelmel.model.Image;
import com.nextep.pelmel.model.User;
import com.nextep.pelmel.providers.CountersProvider;
import com.nextep.pelmel.providers.SnippetInfoProvider;
import com.nextep.pelmel.services.ActionManager;
import com.nextep.pelmel.services.ConversionService;

import java.util.Collections;
import java.util.List;

/**
 * Created by cfondacci on 28/07/15.
 */
public class UserInfoProvider implements SnippetInfoProvider, CountersProvider {

    private User user;
    public UserInfoProvider(User user) {
        this.user = user;
    }
    @Override
    public CalObject getItem() {
        return user;
    }

    @Override
    public String getTitle() {
        return user.getName();
    }

    @Override
    public String getSubtitle() {
        return Strings.getText(user.isOnline() ? R.string.online : R.string.offline);
    }

    @Override
    public Bitmap getSubtitleIcon() {
        int res = user.isOnline() ? R.drawable.online : R.drawable.offline;
        return BitmapFactory.decodeResource(PelMelApplication.getInstance().getResources(), res);
    }

    @Override
    public Image getSnippetImage() {
        return user.getThumb();
    }

    @Override
    public int getLikesCount() {
        return user.getLikeCount();
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
        return user.getDescription();
    }

    @Override
    public String getItemTypeLabel() {
        return null;
    }

    @Override
    public String getCity() {
        return user.getCityName();
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
        final ConversionService conversionService = PelMelApplication.getConversionService();
        return conversionService.getDistanceStringForMiles(user.getRawDistanceMiles());
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
        int rowCount = 0;
        if(!user.getLikedPlaces().isEmpty()) {
            rowCount++;
        }
        if(!user.getLikedUsers().isEmpty()) {
            rowCount++;
        }
        return rowCount;
    }

    @Override
    public List<CalObject> getThumbListObjects(int row) {
        if(row==0 && !user.getLikedPlaces().isEmpty()) {
            return (List)user.getLikedPlaces();
        } else {
            return (List)user.getLikedUsers();
        }
    }

    @Override
    public String getThumbListSectionTitle(int row) {
        if(row==0 && !user.getLikedPlaces().isEmpty()) {
            return Strings.getCountedText(R.string.thumbs_section_liked_places_singular,R.string.thumbs_section_liked_places,user.getLikedPlaces().size()).toString();
        } else {
            return Strings.getCountedText(R.string.thumbs_section_liked_users_singular, R.string.thumbs_section_liked_users, user.getLikedUsers().size()).toString();
        }
    }

    @Override
    public Bitmap getThumbListSectionIcon(int row) {
        Resources resources = PelMelApplication.getInstance().getResources();
        if(row==0 && !user.getLikedPlaces().isEmpty()) {
            return BitmapFactory.decodeResource(resources,R.drawable.ovv_icon_check_white);
        } else {
            return BitmapFactory.decodeResource(resources,R.drawable.snp_icon_like_white);
        }
    }

    @Override
    public String getCounterLabelAtIndex(int index) {
        switch(index) {
            case COUNTER_LIKE:
                return Strings.getCountedText(R.string.counter_likes_singular,R.string.counter_likes,user.getLikeCount()).toString();
            case COUNTER_CHECKIN:
                return null;

            case COUNTER_CHAT:
                return Strings.getText(R.string.action_chat);
        }
        return null;
    }

    @Override
    public String getCounterActionLabelAtIndex(int index) {
        int res = 0;
        switch(index) {
            case COUNTER_LIKE:
                if(user.isLiked()) {
                    res = R.string.action_unlike;
                } else {
                    res = R.string.action_like;
                }
                break;
            case COUNTER_CHECKIN:
                break;
            case COUNTER_CHAT:
                res = R.string.action_chat;
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
                return user.isLiked();
            case COUNTER_CHECKIN:
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
                mgr.executeAction(selected ? Action.UNLIKE : Action.LIKE, user, new ActionManager.ActionCallback() {
                    @Override
                    public void actionCompleted(boolean isSucess, Object result) {
                        if(!selected) {
                            PelMelApplication.getUiService().showInfoMessage(context, R.string.alert_like_success_title, R.string.alert_like_user_success);
                        } else {
                            PelMelApplication.getUiService().showInfoMessage(context, R.string.alert_unlike_success_title, R.string.alert_unlike_user_success);
                        }
                    refreshable.updateData();
                    }
                });
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
        return index != COUNTER_CHECKIN;
    }
}
