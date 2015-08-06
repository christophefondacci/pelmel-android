package com.nextep.pelmel.model;

/**
 * Created by cfondacci on 23/07/15.
 */
public interface RecurringEvent extends Event {

    String CAL_TYPE_RECURRING = "SERI";
    EventType getEventType();
    RecurrencyType getRecurrencyType();
    int getStartHour();
    int getStartMinute();
    int getEndHour();
    int getEndMinute();
    boolean isMonday();
    boolean isTuesday();
    boolean isWednesday();
    boolean isThursday();
    boolean isFriday();
    boolean isSaturday();
    boolean isSunday();

    void setEventType(EventType type);
    void setRecurrencyType(RecurrencyType recurrencyType);
    void setStartHour(int startHour);
    void setStartMinute(int startMinute);
    void setEndHour(int endHour);
    void setEndMinute(int endMinute);
    void setMonday(boolean monday);
    void setTuesday(boolean tuesday);
    void setWednesday(boolean wednesday);
    void setThursday(boolean thursday);
    void setFriday(boolean friday);
    void setSaturday(boolean saturday);
    void setSunday(boolean sunday);
}
