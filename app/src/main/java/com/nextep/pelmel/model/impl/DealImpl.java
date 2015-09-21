package com.nextep.pelmel.model.impl;

import com.nextep.pelmel.model.CalObject;
import com.nextep.pelmel.model.Deal;
import com.nextep.pelmel.model.DealStatus;
import com.nextep.pelmel.model.DealType;
import com.nextep.pelmel.model.base.AbstractCalObject;

import java.util.Date;

/**
 * Created by cfondacci on 14/09/15.
 */
public class DealImpl extends AbstractCalObject implements Deal  {

    private CalObject relatedObject;
    private DealType dealType;
    private DealStatus dealStatus;
    private Date startDate;
    private Date lastUsedDate;
    private int usedToday;
    private int maxUses;

    @Override
    public CalObject getRelatedObject() {
        return relatedObject;
    }

    @Override
    public void setRelatedObject(CalObject relatedObject) {
        this.relatedObject = relatedObject;
    }

    @Override
    public DealStatus getDealStatus() {
        return dealStatus;
    }

    @Override
    public void setDealStatus(DealStatus dealStatus) {
        this.dealStatus = dealStatus;
    }

    @Override
    public DealType getDealType() {
        return dealType;
    }

    public void setDealType(DealType dealType) {
        this.dealType = dealType;
    }

    @Override
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Override
    public Date getLastUsedDate() {
        return lastUsedDate;
    }

    @Override
    public void setLastUsedDate(Date lastUsedDate) {
        this.lastUsedDate = lastUsedDate;
    }

    @Override
    public int getUsedToday() {
        return usedToday;
    }

    @Override
    public void setUsedToday(int usedToday) {
        this.usedToday = usedToday;
    }

    @Override
    public int getMaxUses() {
        return maxUses;
    }

    @Override
    public void setMaxUses(int maxUses) {
        this.maxUses = maxUses;
    }
}
