package com.nextep.pelmel.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nextep.pelmel.R;
import com.nextep.pelmel.listeners.MessageCallback;
import com.nextep.pelmel.model.ChatMessage;
import com.nextep.pelmel.model.User;
import com.nextep.pelmel.model.db.Message;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;

public class MessageAdapter extends RealmBaseAdapter<Message> {

    private static final DateFormat DATE_FORMATTER = SimpleDateFormat
            .getDateTimeInstance();
    private static final int VIEW_TYPE_MSG_SELF = 0;
    private static final int VIEW_TYPE_MSG_OTHER = 1;
    private static final int VIEW_TYPE_STATUS = 2;
    private final User user;
    private final String otherUserKey;
    private final MessageCallback messageCallback;
    private final Context context;
    private final LayoutInflater layoutInflater;

    public MessageAdapter(Context context, RealmResults<Message> messages, User user, String otherUserKey,
                          MessageCallback messageCallback) {
        super(context, messages,true);
        this.context = context;
        this.user = user;
        this.otherUserKey = otherUserKey;
        this.messageCallback = messageCallback;
        this.layoutInflater = LayoutInflater.from(context);
    }

    protected Context getContext() {
        return context;
    }

    static List<ChatMessage> convertList(List<ChatMessage> messages) {
        messages.add(null);
        return messages;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        final Message msg = getItem(position);
        if (isSelfMsg(msg)) {
            return VIEW_TYPE_MSG_SELF;
        } else {
            return VIEW_TYPE_MSG_OTHER;
        }
    }

    private boolean isSelfMsg(Message msg) {
        return msg.getFrom().getItemKey().equals(user.getKey());
    }

    @Override
    public View getView(int position, View c, ViewGroup parent) {
        ViewHolder viewHolder;
        View convertView = c;
        final Message msg = getItem(position);

        if (convertView == null || convertView.getTag() == null) {
            viewHolder = new ViewHolder();
            // If we are not on the status line (i.e. we have a non null
            // message)
            if (isSelfMsg(msg)) {
                convertView = layoutInflater.inflate(
                        R.layout.list_row_chat_right, null);
            } else {
                convertView = layoutInflater.inflate(
                        R.layout.list_row_chat_left, null);
            }

            viewHolder.dateView = (TextView) convertView
                    .findViewById(R.id.msgDate);
            viewHolder.textView = (TextView) convertView
                    .findViewById(R.id.chat_message);
            viewHolder.imageView = (ImageView) convertView
                    .findViewById(R.id.chat_image);
            viewHolder.nicknameText = (TextView)convertView.findViewById(R.id.usernameLabel);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (msg != null) {
            viewHolder.textView.setText(msg.getMessageText());
            viewHolder.dateView.setText(DATE_FORMATTER.format(msg.getMessageDate()));
            final String thumbUrl = msg.getFrom().getImageThumbUrl();
            viewHolder.imageView.setImageResource(R.drawable.no_photo_profile_small);
            if (thumbUrl != null) {
                ImageLoader.getInstance().displayImage(thumbUrl,viewHolder.imageView);
//                PelMelApplication.getImageService().displayImage(thumb, true,
//                        viewHolder.imageView);
            }
        }
        return convertView;
    }

    private class ViewHolder {
        TextView textView;
        TextView dateView;
        ImageView imageView;
        TextView nicknameText;
    }

}
