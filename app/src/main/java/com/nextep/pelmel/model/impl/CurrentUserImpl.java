package com.nextep.pelmel.model.impl;

import com.nextep.pelmel.model.CurrentUser;
import com.nextep.pelmel.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cfondacci on 25/09/15.
 */
public class CurrentUserImpl extends UserImpl implements CurrentUser {

    private List<User> networkPendingApprovals = new ArrayList<>();
    private List<User> networkPendingRequests = new ArrayList<>();
    private List<User> networkUsers = new ArrayList<>();

    @Override
    public List<User> getNetworkPendingApprovals() {
        return networkPendingApprovals;
    }

    @Override
    public List<User> getNetworkPendingRequests() {
        return networkPendingRequests;
    }

    @Override
    public List<User> getNetworkUsers() {
        return networkUsers;
    }

    public void setNetworkPendingApprovals(List<User> networkPendingApprovals) {
        this.networkPendingApprovals = networkPendingApprovals;
    }

    public void setNetworkPendingRequests(List<User> networkPendingRequests) {
        this.networkPendingRequests = networkPendingRequests;
    }

    public void setNetworkUsers(List<User> networkUsers) {
        this.networkUsers = networkUsers;
    }
}
