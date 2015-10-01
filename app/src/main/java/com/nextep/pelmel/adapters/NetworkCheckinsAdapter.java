package com.nextep.pelmel.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.model.CurrentUser;
import com.nextep.pelmel.model.Place;
import com.nextep.pelmel.model.User;
import com.nextep.pelmel.services.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cfondacci on 29/09/15.
 */
public class NetworkCheckinsAdapter extends BaseAdapter {

    public static final int VIEW_TYPE_NO_CHECKIN = 1;
    public static final int VIEW_TYPE_CHECKINS = 0;
    private Context context;
    private Map<String, List<User>> usersInPlaceMap = new HashMap<>();
    private List<Place> places = new ArrayList<>();
    private UserService userService;
    public NetworkCheckinsAdapter(Context context) {
        userService = PelMelApplication.getUserService();
        final CurrentUser currentUser = userService.getLoggedUser();
        for(User user : currentUser.getNetworkUsers()) {

            // Getting current user checked in place
            final Place checkedInPlace = userService.getCheckedInPlace(user);

            // Have we found anything?
            if(checkedInPlace != null) {

                // If yes we add the user to the list of users in that place
                List<User> placeUsers = usersInPlaceMap.get(checkedInPlace.getKey());
                if (placeUsers == null) {

                    // Creating structure and filling map and places list the first time we hit this place
                    placeUsers = new ArrayList<>();
                    usersInPlaceMap.put(checkedInPlace.getKey(), placeUsers);
                    places.add(checkedInPlace);
                }

                // Augmenting the list of users at this location
                placeUsers.add(user);
            }
        }

        // Sorting the list of places by number of users
        Collections.sort(places, new Comparator<Place>() {
            @Override
            public int compare(Place lhs, Place rhs) {

                List<User> u1 = usersInPlaceMap.get(lhs.getKey());
                List<User> u2 = usersInPlaceMap.get(rhs.getKey());
                return u2.size()-u1.size();
            }
        });
    }

    @Override
    public int getCount() {
        return Math.max(places.size(),1);
    }

    @Override
    public Object getItem(int position) {
        return position < places.size() ? places.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if(places.isEmpty()) {
            return VIEW_TYPE_NO_CHECKIN;
        } else {
            return VIEW_TYPE_CHECKINS;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    public List<User> getUsers(Place p) {
        return usersInPlaceMap.get(p.getKey());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView != null) {

        }
        return null;
    }
}
