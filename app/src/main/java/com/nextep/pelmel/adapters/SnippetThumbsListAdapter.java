package com.nextep.pelmel.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nextep.pelmel.R;
import com.nextep.pelmel.providers.SnippetInfoProvider;
import com.nextep.pelmel.views.HorizontalListView;

/**
 * Created by cfondacci on 28/07/15.
 */
public class SnippetThumbsListAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {

    private Context context;
    private SnippetInfoProvider infoProvider;
    private LayoutInflater layoutInflater;

    public SnippetThumbsListAdapter(Context context, SnippetInfoProvider infoProvider) {
        this.context = context;
        this.infoProvider = infoProvider;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return infoProvider.getThumbListsRowCount()*2;
    }

    @Override
    public Object getItem(int position) {
        if(position%2==0) {
            return infoProvider.getThumbListSectionTitle(position);
        } else {
            return infoProvider.getThumbListObjects(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if(position%2==0) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(position%2 == 0) {
            if(convertView == null || convertView.getTag() == null) {
                convertView = layoutInflater.inflate(R.layout.section_title,parent,false);
                convertView.setTag(convertView.findViewById(R.id.sectionTitleLabel));
            }
            final TextView titleLabel = (TextView)convertView.getTag();
            titleLabel.setText(infoProvider.getThumbListSectionTitle(position/2));
        } else {
            if(convertView == null || convertView.getTag() == null) {
                convertView = layoutInflater.inflate(R.layout.list_row_thumbs_list,parent,false);
                convertView.setTag(convertView.findViewById(R.id.thumbsListView));
            }
            final HorizontalListView listView = (HorizontalListView)convertView.getTag();
            final CALObjectThumbAdapter adapter = new CALObjectThumbAdapter(context, infoProvider.getThumbListObjects((position-1)/2),R.dimen.thumbs_overview_size);

            listView.setAdapter(adapter);
            listView.setOnItemClickListener(adapter);
        }
        return convertView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
