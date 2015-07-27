package com.nextep.pelmel.helpers;

import android.graphics.Typeface;
import android.widget.TextView;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.R;
import com.nextep.pelmel.model.CalObject;
import com.nextep.pelmel.model.Event;
import com.nextep.pelmel.model.RecurringEvent;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by cfondacci on 22/07/15.
 */
public final class Strings {
    private static  DateFormat eventDayFormat =  new SimpleDateFormat("EEE");
    private static  DateFormat eventTimeFormat =  DateFormat.getTimeInstance(DateFormat.SHORT);
    private static Typeface typeface;

    private Strings() {
    }

    public static String getText(int res) {
        return PelMelApplication.getInstance().getText(res).toString();
    }

    public static CharSequence getCountedText(int resSingular, int resPlural, int count) {
        int res = -1;
        if(count<=1) {
            res = resSingular;
        } else {
            res = resPlural;
        }
        return MessageFormat.format(getText(res).toString(), count);
    }

    public static String getEventDate(Event event, boolean isStart) {
        Date date;
        boolean timeOnly = false;
        if(isStart) {
            date = event.getStartDate();
        } else {
            final long delta = event.getEndDate().getTime() - event.getStartDate().getTime();
            if(delta < 86400000) {
                timeOnly = true;
            }
        }
        return getEventDate(isStart ? event.getStartDate() : event.getEndDate(), timeOnly);
    }
    private static String getEventDate(Date date, boolean timeOnly) {
        StringBuilder buf = new StringBuilder();
        if(!timeOnly) {
            final String dayOfWeek = eventDayFormat.format(date);
            buf.append(dayOfWeek);
            buf.append(' ');
            final DateFormat dateFormat = getShortDateInstanceWithoutYears(Locale.getDefault());
            final String day = dateFormat.format(date);
            buf.append(day);
            buf.append(" ");
        }
        final String time = eventTimeFormat.format(date);
        buf.append(time);
        return buf.toString();
    }
    private static DateFormat getShortDateInstanceWithoutYears(Locale locale) {
        SimpleDateFormat sdf = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
        sdf.applyPattern(sdf.toPattern().replaceAll("[^\\p{Alpha}]*y+[^\\p{Alpha}]*", ""));
        return sdf;
    }

    public static String getName(CalObject obj) {
        if(obj instanceof Event) {
            final Event e = (Event) obj;
            if(e.getName()!=null && !e.getName().trim().isEmpty()) {
                return e.getName();
            } else if(e instanceof RecurringEvent) {
                switch(((RecurringEvent) e).getEventType()) {
                    case HAPPY_HOUR:
                        return getText(R.string.event_type_happyhour);
                    case THEME:
                        return getText(R.string.event_type_themenight);
                    case OPENING:
                        return getText(R.string.event_type_opening);

                }
            }
        } else if(obj instanceof CalObject) {
            return obj.getName();
        }
        return null;
    }

    public static void setFontFamily(TextView textView) {
        if(typeface == null) {
            synchronized (Strings.class) {
                typeface = Typeface.createFromAsset(PelMelApplication.getInstance().getAssets(),"OpenSans-Regular.ttf");
            }
        }
        textView.setTypeface(typeface);
    }
}
