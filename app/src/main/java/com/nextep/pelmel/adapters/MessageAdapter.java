package com.nextep.pelmel.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.R;
import com.nextep.pelmel.listeners.MessageCallback;
import com.nextep.pelmel.model.ChatMessage;
import com.nextep.pelmel.model.Image;
import com.nextep.pelmel.model.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class MessageAdapter extends ArrayAdapter<ChatMessage> {

    private static final DateFormat DATE_FORMATTER = SimpleDateFormat
            .getDateTimeInstance();
    private static final int VIEW_TYPE_MSG_SELF = 0;
    private static final int VIEW_TYPE_MSG_OTHER = 1;
    private static final int VIEW_TYPE_STATUS = 2;
    private final User user;
    private final String otherUserKey;
    private final MessageCallback messageCallback;

    public MessageAdapter(Context context, int textViewResourceId,
                          List<ChatMessage> objects, User user, String otherUserKey,
                          MessageCallback messageCallback) {
        super(context, textViewResourceId, convertList(objects));
        this.user = user;
        this.otherUserKey = otherUserKey;
        this.messageCallback = messageCallback;
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
        final ChatMessage msg = getItem(position);
        if (msg == null) {
            return VIEW_TYPE_STATUS;
        } else if (isSelfMsg(msg)) {
            return VIEW_TYPE_MSG_SELF;
        } else {
            return VIEW_TYPE_MSG_OTHER;
        }
    }

    private boolean isSelfMsg(ChatMessage msg) {
        return msg.getFrom().getKey().equals(user.getKey());
    }

    @Override
    public View getView(int position, View c, ViewGroup parent) {
        ViewHolder viewHolder;
        View convertView = c;
        final ChatMessage msg = getItem(position);

        if (convertView == null) {
            viewHolder = new ViewHolder();
            // If we are not on the status line (i.e. we have a non null
            // message)
            if (msg != null) {
                if (isSelfMsg(msg)) {
                    convertView = LayoutInflater.from(getContext()).inflate(
                            R.layout.list_row_chat_right, null);
                } else {
                    convertView = LayoutInflater.from(getContext()).inflate(
                            R.layout.list_row_chat_left, null);
                }

                viewHolder.dateView = (TextView) convertView
                        .findViewById(R.id.msgDate);
                viewHolder.textView = (TextView) convertView
                        .findViewById(R.id.chat_message);
                viewHolder.imageView = (ImageView) convertView
                        .findViewById(R.id.chat_image);
                viewHolder.bubbleView = (ImageView) convertView
                        .findViewById(R.id.bg_bubble);

            } else {
                if (otherUserKey == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(
                            R.layout.list_row_chat_info, null);
                } else {
                    convertView = LayoutInflater.from(getContext()).inflate(
                            R.layout.list_row_chat_text, null);
                    viewHolder.textView = (EditText) convertView
                            .findViewById(R.id.chat_message);
                    final TextView textView = viewHolder.textView;
                    final Button sendButton = (Button) convertView
                            .findViewById(R.id.chat_send_button);
                    sendButton.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (otherUserKey != null) {
                                final String message = textView.getText()
                                        .toString();
                                if (message != null
                                        && !"".equals(message.trim())) {
                                    PelMelApplication.getMessageService()
                                            .sendMessage(user, otherUserKey,
                                                    message, messageCallback);
                                }
                                textView.setText("");
                            }
                        }
                    });
                }
            }
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (msg != null) {
            viewHolder.textView.setText(msg.getMessage());
            viewHolder.dateView.setText(DATE_FORMATTER.format(msg.getDate()));
            final Image thumb = msg.getFrom().getThumb();
            viewHolder.imageView.setImageResource(R.drawable.no_photo);
            if (thumb != null) {
                PelMelApplication.getImageService().displayImage(thumb, true,
                        viewHolder.imageView);
            }
        }
        return convertView;
    }

    private class ViewHolder {
        TextView textView;
        TextView dateView;
        ImageView imageView;
        ImageView bubbleView;
    }

}
