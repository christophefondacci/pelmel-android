package com.nextep.pelmel.helpers;

import com.nextep.pelmel.PelMelApplication;

import java.text.MessageFormat;

/**
 * Created by cfondacci on 22/07/15.
 */
public final class Strings {
    private Strings() {}

    public static CharSequence getText(int res) {
        return PelMelApplication.getInstance().getText(res);
    }

    public static CharSequence getCountedText(int resSingular, int resPlural, int count) {
        int res = -1;
        if(count<=1) {
            res = resSingular;
        } else {
            res = resPlural;
        }
        return MessageFormat.format(getText(res).toString(),count);
    }
}
