package com.nextep.pelmel.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nextep.pelmel.R;
import com.nextep.pelmel.helpers.PelmelFont;
import com.nextep.pelmel.helpers.Strings;

/**
 * Created by cfondacci on 26/10/15.
 */
public class IntroFragment extends Fragment {

    private ImageView introImage;
    private TextView introLabel;
    private int imageRes;
    private int labelRes;

    public void setImageRes(int imageRes) {
        this.imageRes = imageRes;
    }

    public void setLabelRes(int labelRes) {
        this.labelRes = labelRes;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.intro_page,container,false);

        introImage = (ImageView)view.findViewById(R.id.introImage);
        introLabel = (TextView)view.findViewById(R.id.introText);

        introImage.setImageResource(imageRes);
        introLabel.setText(labelRes);
        Strings.setFontFamily(introLabel, PelmelFont.SOURCE_SANSPRO_LIGHT);

        return view;
    }
}
