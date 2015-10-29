package com.nextep.pelmel.activities;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.PelMelConstants;
import com.nextep.pelmel.R;

/**
 * Created by cfondacci on 26/10/15.
 */
public class IntroActivity extends AppIntro {

    @Override
    public void init(Bundle savedInstanceState) {
        final IntroFragment f1 = new IntroFragment();
        f1.setImageRes(R.drawable.intro_bg_1);
        f1.setLabelRes(R.string.intro_1);

        addSlide(f1);
        final IntroFragment f2 = new IntroFragment();
        f2.setImageRes(R.drawable.intro_bg_2);
        f2.setLabelRes(R.string.intro_2);

        addSlide(f2);
        final IntroFragment f3 = new IntroFragment();
        f3.setImageRes(R.drawable.intro_bg_4);
        f3.setLabelRes(R.string.intro_3);
        addSlide(f3);

        final IntroLoginFragment f4 = new IntroLoginFragment();
        addSlide(f4);
        showDoneButton(false);

        // Saving those credentials
        setOffScreenPageLimit(1);
        final SharedPreferences prefs = PelMelApplication.getInstance()
                .getSharedPreferences(PelMelConstants.PREFS_NAME, 0);
        if(prefs.getBoolean(PelMelConstants.PREF_INTRODONE,false)) {
            getPager().setCurrentItem(3);
        }
    }

    @Override
    public void onSkipPressed() {
        getPager().setCurrentItem(3);
    }

    @Override
    public void onDonePressed() {

    }
}
