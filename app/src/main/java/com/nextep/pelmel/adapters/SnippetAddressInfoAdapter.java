package com.nextep.pelmel.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.R;
import com.nextep.pelmel.providers.SnippetInfoProvider;

import java.util.List;

/**
 * Created by cfondacci on 25/07/15.
 */
public class SnippetAddressInfoAdapter extends BaseSnippetInfoAdapter {
    private SnippetInfoProvider infoProvider;

    public SnippetAddressInfoAdapter(Context context, SnippetInfoProvider infoProvider) {
        super(context);
        this.infoProvider = infoProvider;
    }

    @Override
    public Object getItem(int position) {
        final List<String> components = infoProvider.getAddressComponents();
        if(position < components.size()) {
            return infoProvider.getAddressComponents().get(position);
        } else {
            return infoProvider.getCity();
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public int getCount() {
        return infoProvider.getAddressComponents().size() + 1;
    }

    @Override
    protected String getInfoText(int position) {
        return (String)getItem(position);
    }

    @Override
    protected Bitmap getInfoImage(int position) {
        if(position == infoProvider.getAddressComponents().size()) {
            return BitmapFactory.decodeResource(PelMelApplication.getInstance().getResources(), R.drawable.snp_icon_city);
        }
        return null;
    }
}
