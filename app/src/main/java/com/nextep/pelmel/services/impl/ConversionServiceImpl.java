package com.nextep.pelmel.services.impl;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.R;
import com.nextep.pelmel.model.Localized;
import com.nextep.pelmel.model.Place;
import com.nextep.pelmel.services.ConversionService;

import java.text.MessageFormat;

/**
 * Created by cfondacci on 22/07/15.
 */
public class ConversionServiceImpl implements ConversionService {

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

        return MessageFormat.format(currentTemplate.toString(),String.valueOf(currentValue));
    }

    @Override
    public double getDistanceTo(Localized localized) {
        if(localized instanceof Place) {
            return ((Place)localized).getDistance();
        }
        throw new UnsupportedOperationException("getDistanceTo() not implemented yet for other types than Place");
    }
}
