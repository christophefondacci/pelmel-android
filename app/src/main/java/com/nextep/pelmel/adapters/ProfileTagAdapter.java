package com.nextep.pelmel.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.R;
import com.nextep.pelmel.model.Tag;
import com.nextep.pelmel.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cfondacci on 05/08/15.
 */
public class ProfileTagAdapter extends BaseAdapter {

    private Context context;
    private User user;
    private LayoutInflater layoutInflater;
    private List<Tag> allTags;
    private Map<String,Tag> userTagsMap;
    public ProfileTagAdapter(Context context, User user) {
        this.context = context;
        this.user = user;
        this.layoutInflater = LayoutInflater.from(context);
        allTags = PelMelApplication.getTagService().listTags();
        invalidate();
    }

    public void invalidate() {
        userTagsMap = new HashMap<>();
        for(Tag t : user.getTags()) {
            userTagsMap.put(t.getCode(),t);
        }
    }
    @Override
    public int getCount() {
        return allTags.size();
    }

    @Override
    public Object getItem(int position) {
        return allTags.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if(convertView == null || convertView.getTag() == null) {
            convertView = layoutInflater.inflate(R.layout.list_row_profile_tag,parent,false);

            viewHolder = new ViewHolder();
            viewHolder.tagImage = (ImageView)convertView.findViewById(R.id.tagImage);
            viewHolder.tagLabel = (TextView)convertView.findViewById(R.id.tagLabel);
            viewHolder.checkmark = (TextView)convertView.findViewById(R.id.tagCheckmark);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        // Updating tag info
        final Tag tag = allTags.get(position);
        viewHolder.tagImage.setImageBitmap(BitmapFactory.decodeResource(PelMelApplication.getInstance().getResources(), PelMelApplication.getTagService().getImageResource(tag)));
        viewHolder.tagLabel.setText(tag.getLabel());

        if(userTagsMap.get(tag.getCode())!=null) {
            viewHolder.checkmark.setVisibility(View.VISIBLE);
            convertView.setBackgroundResource(R.color.pelmelActionBar);
        } else {
            viewHolder.checkmark.setVisibility(View.INVISIBLE);
            convertView.setBackgroundResource(R.color.pelmelBackground);
        }
        return convertView;
    }

    class ViewHolder {
        ImageView tagImage;
        TextView tagLabel;
        TextView checkmark;
    }
}
