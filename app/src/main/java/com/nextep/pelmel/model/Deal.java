package com.nextep.pelmel.model;

import java.util.Date;

/**
 * Created by cfondacci on 14/09/15.
 */
public interface Deal extends CalObject {

    /**
     * The object on which the deal is offered
     * @return
     */
    CalObject getRelatedObject();

    void setRelatedObject(CalObject object);

    /**
     * The type of deal offered
     * @return the DealType
     */
    DealType getDealType();
    void setDealType(DealType dealType);

    /**
     * The current status of the deal
     * @return the DealStatus
     */
    DealStatus getDealStatus();
    void setDealStatus(DealStatus dealStatus);

    /**
     * The initial start date of that deal
     * @return the deal first start date
     */
    Date getStartDate();
    void setStartDate(Date startDate);

    /**
     * The date of last use of that deal for the current user
     * @return the deal's last use date for current user
     */
    Date getLastUsedDate();
    void setLastUsedDate(Date lastUsedDate);

    /**
     * The number of deal uses today
     * @return today's deal uses
     */
    int getUsedToday();
    void setUsedToday(int usedToday);

    /**
     * The maximum number of uses per day
     * @return the max uses per day or 0 if no limit
     */
    int getMaxUses();
    void setMaxUses(int maxUses);
}
