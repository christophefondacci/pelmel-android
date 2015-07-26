package com.nextep.pelmel.model.impl;

import com.nextep.pelmel.model.EventType;
import com.nextep.pelmel.model.RecurrencyType;
import com.nextep.pelmel.model.RecurringEvent;

/**
 * Created by cfondacci on 23/07/15.
 */
public class RecurringEventImpl extends EventImpl implements RecurringEvent {
    private EventType eventType;
    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;
    private boolean monday;
    private boolean tuesday;
    private boolean wednesday;
    private boolean thursday;
    private boolean friday;
    private boolean saturday;
    private boolean sunday;
    private RecurrencyType recurrencyType;

    @Override
    public EventType getEventType() {
        return eventType;
    }

    @Override
    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    @Override
    public int getStartHour() {
        return startHour;
    }

    @Override
    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    @Override
    public int getStartMinute() {
        return startMinute;
    }

    @Override
    public void setStartMinute(int startMinute) {
        this.startMinute = startMinute;
    }

    @Override
    public int getEndHour() {
        return endHour;
    }

    @Override
    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    @Override
    public int getEndMinute() {
        return endMinute;
    }

    @Override
    public void setEndMinute(int endMinute) {
        this.endMinute = endMinute;
    }

    @Override
    public boolean isMonday() {
        return monday;
    }

    @Override
    public void setMonday(boolean monday) {
        this.monday = monday;
    }

    @Override
    public boolean isTuesday() {
        return tuesday;
    }

    @Override
    public void setTuesday(boolean tuesday) {
        this.tuesday = tuesday;
    }

    @Override
    public boolean isWednesday() {
        return wednesday;
    }

    @Override
    public void setWednesday(boolean wednesday) {
        this.wednesday = wednesday;
    }

    @Override
    public boolean isThursday() {
        return thursday;
    }

    @Override
    public void setThursday(boolean thursday) {
        this.thursday = thursday;
    }

    @Override
    public boolean isFriday() {
        return friday;
    }

    @Override
    public void setFriday(boolean friday) {
        this.friday = friday;
    }

    @Override
    public boolean isSaturday() {
        return saturday;
    }

    @Override
    public void setSaturday(boolean saturday) {
        this.saturday = saturday;
    }

    @Override
    public boolean isSunday() {
        return sunday;
    }

    @Override
    public void setSunday(boolean sunday) {
        this.sunday = sunday;
    }

    @Override
    public void setRecurrencyType(RecurrencyType recurrencyType) {
        this.recurrencyType = recurrencyType;
    }

    @Override
    public RecurrencyType getRecurrencyType() {
        return recurrencyType;
    }
}
