package com.nextep.pelmel.services.impl;

import android.location.Location;
import android.util.Log;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.R;
import com.nextep.pelmel.helpers.GeoUtils;
import com.nextep.pelmel.helpers.Strings;
import com.nextep.pelmel.model.EventType;
import com.nextep.pelmel.model.Localized;
import com.nextep.pelmel.model.Place;
import com.nextep.pelmel.model.RecurringEvent;
import com.nextep.pelmel.services.ConversionService;
import com.nextep.pelmel.services.LocalizationService;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cfondacci on 22/07/15.
 */
public class ConversionServiceImpl implements ConversionService {
    private final static String LOG_TAG = "ConversionService";

    @Override
    public boolean isMetric() {
        return false;
    }

    private CharSequence getString(int res) {
        return PelMelApplication.getInstance().getText(res);
    }
    @Override
    public String getDistanceStringForMiles(double d) {
        CharSequence currentTemplate;
        double currentValue = 0;
        double distance = d*1609.34;
        if(isMetric()) {
            if(distance > 1000) {
                currentTemplate = getString(R.string.distance_kilometers);
                currentValue = distance/100;
                currentValue = (double)(int)currentValue;
                currentValue = currentValue/10;
            } else {
                currentTemplate = getString(R.string.distance_meters);
                currentValue = (double)(int)distance;
            }
        } else {
            double miles = distance / 1609.34;
            if(miles < 0.5) {
                double feet = distance / 0.3048;
                currentTemplate = getString(R.string.distance_feet);
                currentValue = (double)(int)feet;
            } else {
                currentTemplate = getString(R.string.distance_miles);
                currentValue = (double)(int)(miles*10.0);
                currentValue = Math.round(currentValue);
                currentValue = currentValue / 10.0;
            }
        }

        return MessageFormat.format(currentTemplate.toString(), String.valueOf(currentValue));
    }

    @Override
    public double getDistanceTo(Localized localized) {
        final LocalizationService locService = PelMelApplication.getLocalizationService();
        final Location loc = locService.getLocation();

        double dist = GeoUtils.distance(loc.getLatitude(),loc.getLongitude(),localized.getLatitude(),localized.getLongitude());
        return dist;
//        if(localized instanceof Place) {
//            return ((Place)localized).getDistance();
//        }
//        throw new UnsupportedOperationException("getDistanceTo() not implemented yet for other types than Place");
    }

    @Override
    public Map<EventType, List<RecurringEvent>> buildTypedHoursMap(Place place) {
        final Collection<RecurringEvent> hours = place.getRecurringEvents();
        final Map<EventType, List<RecurringEvent>> typedHoursMap = new HashMap<>();

        // Iterating over every hour entry
        for(RecurringEvent hour : hours) {
            // Retrieving typed list of events
            List<RecurringEvent> typedEvents = typedHoursMap.get(hour.getEventType());
            // If null we create a new entry
            if(typedEvents == null) {
                typedEvents = new ArrayList<>();
                typedHoursMap.put(hour.getEventType(),typedEvents);
            }

            // Adding our hour to the list
            typedEvents.add(hour);
        }
        return typedHoursMap;
    }

    @Override
    public String getRecurringEventLabel(RecurringEvent event) {
        DateFormat formatter = new SimpleDateFormat();
        String[] daySymbols = DateFormatSymbols.getInstance().getShortWeekdays();


        // Building a list from days
        List<Boolean> enabledList = new ArrayList<>();
        enabledList.add(event.isSunday());
        enabledList.add(event.isMonday());
        enabledList.add(event.isTuesday());
        enabledList.add(event.isWednesday());
        enabledList.add(event.isThursday());
        enabledList.add(event.isFriday());
        enabledList.add(event.isSaturday());



        Integer start = null;
        StringBuilder buf = new StringBuilder();
        String sep = "";

        Integer prefixRes = null;
        if(event.getRecurrencyType()!=null) {
            switch (event.getRecurrencyType()) {
                case FIRST:
                    prefixRes = R.string.calendar_repeat_first;
                    break;
                case SECOND:
                    prefixRes = R.string.calendar_repeat_second;
                    break;
                case THIRD:
                    prefixRes = R.string.calendar_repeat_third;
                    break;
                case FOURTH:
                    prefixRes = R.string.calendar_repeat_fourth;
                    break;
            }
            if (prefixRes != null) {
                buf.append(Strings.getText(prefixRes));
                buf.append(' ');
            }
        }
        boolean allTrue = true;
        int i = 1;
        while (i <= enabledList.size()) {
            // Is this day active?
            final Boolean enabled = enabledList.get(i-1);
            allTrue = allTrue && enabled;
            // If yes and no start, we register it
            if (start == null && enabled) {
                start = i;
            }
            // If not enabled we print last range
            if (!enabled && start != null) {
                buf.append(sep);
                buf.append(daySymbols[start]);
                if (i > start + 1) {
                    buf.append("-"+daySymbols[i-1]);
                }
                sep = ", ";
                start = null;
            }
            i++;
        }
        // Last part may not have been added
        if (start != null) {
            buf.append(sep);
            buf.append(daySymbols[start]);
            if (i > start + 1) {
                buf.append("-" + daySymbols[i-1]);
            }
        }
        if (allTrue) {
            buf = new StringBuilder();
            buf.append(Strings.getText(R.string.calendar_daily));
        }

        // Handling US / european dates
        String localStartTime   = getLocalizedStringForHours(event.getStartHour(),event.getStartMinute());
        String localEndTime     = getLocalizedStringForHours(event.getEndHour(),event.getEndMinute());


        buf.append(" " + localStartTime + "-" + localEndTime);

        return buf.toString();

    }

    private String getLocalizedStringForHours(int hours, int minutes) {
        DateFormat fullClockFormatter = new SimpleDateFormat("HH:mm");

        Date startTime = null;
        String startTimeStr = String.format("%02d",(int)hours%24) + ":" + String.format("%02d",(int)minutes);
        try {
            startTime = fullClockFormatter.parse(startTimeStr);
        } catch (ParseException e) {
            Log.e( LOG_TAG ,"Unparseable time " + startTimeStr,e);
        }

        DateFormat localizedTimeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
        final String localizedTime = localizedTimeFormat.format(startTime);
        
        return localizedTime;
    }
}
