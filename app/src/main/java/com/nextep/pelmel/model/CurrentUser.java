package com.nextep.pelmel.model;

import java.util.List;

/**
 * Created by cfondacci on 25/09/15.
 */
public interface CurrentUser extends User {
    List<User> getNetworkPendingApprovals();
    List<User> getNetworkPendingRequests();
    List<User> getNetworkUsers();
    void setNetworkPendingApprovals(List<User> users);
    void setNetworkPendingRequests(List<User> users);
    void setNetworkUsers(List<User> users);
}
