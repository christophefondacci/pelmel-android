package com.nextep.pelmel.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nextep.pelmel.R;
import com.nextep.pelmel.helpers.Strings;
import com.nextep.pelmel.providers.SnippetInfoProvider;

/**
 * Created by cfondacci on 27/07/15.
 */
public class SnippetDescriptionListAdapter extends BaseAdapter {

    private SnippetInfoProvider infoProvider;
    private Context context;
    private LayoutInflater layoutInflater;

    public SnippetDescriptionListAdapter(Context context,SnippetInfoProvider infoProvider) {
        this.context = context;
        this.infoProvider = infoProvider;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return infoProvider.getDescription() == null || infoProvider.getDescription().isEmpty() ? 0 : 1;
    }

    @Override
    public Object getItem(int position) {
        return infoProvider.getDescription();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null || convertView.getTag() == null)  {
            convertView = layoutInflater.inflate(R.layout.list_row_description,parent,false);
            convertView.setTag(convertView.findViewById(R.id.descriptionLabel));
        }
        final TextView descriptionView = (TextView)convertView.getTag();
        Strings.setFontFamily(descriptionView);
        descriptionView.setText(infoProvider.getDescription());
        return convertView;
    }
}
