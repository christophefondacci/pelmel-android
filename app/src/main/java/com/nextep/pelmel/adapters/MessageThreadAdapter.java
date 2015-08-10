package com.nextep.pelmel.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nextep.pelmel.R;
import com.nextep.pelmel.helpers.Strings;
import com.nextep.pelmel.listeners.MessageCallback;
import com.nextep.pelmel.model.db.MessageRecipient;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;

public class MessageThreadAdapter extends RealmBaseAdapter<MessageRecipient> {

    private static final DateFormat DATE_FORMATTER = SimpleDateFormat
            .getDateTimeInstance();
    private static final int VIEW_TYPE_MSG_SELF = 0;
    private static final int VIEW_TYPE_MSG_OTHER = 1;
    private static final int VIEW_TYPE_STATUS = 2;
    private final MessageCallback messageCallback;
    private final Context context;
    private final LayoutInflater layoutInflater;
    public MessageThreadAdapter(Context context, RealmResults<MessageRecipient> recipients,
                                MessageCallback messageCallback) {
        super(context,recipients,true);
        this.context = context;
        this.messageCallback = messageCallback;
        layoutInflater = LayoutInflater.from(context);
    }


    @Override
    public View getView(int position, View c, ViewGroup parent) {
        ViewHolder viewHolder;
        View convertView = c;
        final MessageRecipient recipient = getItem(position);

        if (convertView == null || convertView.getTag() == null) {
            viewHolder = new ViewHolder();
            // If we are not on the status line (i.e. we have a non null
            // message)
            if (recipient != null) {
                convertView = layoutInflater.inflate(R.layout.list_row_chat_thread, parent,false);

                viewHolder.dateView = (TextView) convertView
                        .findViewById(R.id.msgDate);
                viewHolder.threadNicknameLabel = (TextView) convertView
                        .findViewById(R.id.threadNickname);
                viewHolder.threadContentsLabel = (TextView)convertView.findViewById(R.id.threadMessageCount);
                viewHolder.badgeLabel= (TextView)convertView.findViewById(R.id.badgeLabel);
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.chat_image);
                Strings.setFontFamily(viewHolder.dateView);
                Strings.setFontFamily(viewHolder.threadContentsLabel);
                Strings.setFontFamily(viewHolder.threadNicknameLabel);
            }
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (recipient != null) {
            viewHolder.threadNicknameLabel.setText(recipient.getUsername());
            viewHolder.dateView.setText(DATE_FORMATTER.format(recipient.getLastMessageDate()));
            if(recipient.getUnreadMessageCount()>0) {
                viewHolder.badgeLabel.setText(String.valueOf(recipient.getUnreadMessageCount()));
                viewHolder.badgeLabel.setVisibility(View.VISIBLE);
            } else {
                viewHolder.badgeLabel.setVisibility(View.INVISIBLE);
            }
            viewHolder.threadContentsLabel.setText(recipient.getMessageCount() + " messages");
            if (recipient.getImageThumbUrl() != null && recipient.getImageThumbUrl().startsWith("http")) {
                ImageLoader.getInstance().displayImage(recipient.getImageThumbUrl(),viewHolder.imageView);
            } else {
                viewHolder.imageView.setImageBitmap(
                        BitmapFactory.decodeResource(context.getResources(),R.drawable.no_photo_profile_small));
            }
        }
        return convertView;
    }

    private class ViewHolder {
        TextView threadNicknameLabel;
        TextView threadContentsLabel;
        TextView badgeLabel;
        TextView dateView;
        ImageView imageView;
    }

}
