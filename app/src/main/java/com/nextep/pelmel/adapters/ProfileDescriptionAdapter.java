package com.nextep.pelmel.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nextep.pelmel.R;
import com.nextep.pelmel.model.User;

/**
 * Created by cfondacci on 05/08/15.
 */
public class ProfileDescriptionAdapter extends BaseAdapter implements View.OnClickListener {

    private Context activity;
    private User user;
    private LayoutInflater layoutInflater;

    public ProfileDescriptionAdapter(FragmentActivity activity, User user) {
        this.activity = activity;
        this.user = user;
        this.layoutInflater = LayoutInflater.from(activity);
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int position) {
        return user;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null || convertView.getTag()==null) {
            convertView = layoutInflater.inflate(R.layout.list_row_profile_description,parent,false);

            viewHolder = new ViewHolder();
            viewHolder.descriptionText = (TextView)convertView.findViewById(R.id.descriptionText);
            viewHolder.editButton = (ImageView)convertView.findViewById(R.id.editButton);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.descriptionText.setText(user.getDescription());
        viewHolder.descriptionText.setOnClickListener(this);
        viewHolder.editButton.setOnClickListener(this);
        return convertView;
    }

    @Override
    public void onClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.profileDescriptionHint);

        final EditText descEdit = new EditText(activity);
        descEdit.setInputType(InputType.TYPE_CLASS_TEXT);
        descEdit.setText(user.getDescription());
        descEdit.setMinLines(4);
        descEdit.setBackgroundResource(R.drawable.bg_chat_text);
        descEdit.setSingleLine(false);
        builder.setView(descEdit);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                user.setDescription(descEdit.getText().toString());
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    class ViewHolder {
        TextView descriptionText;
        ImageView editButton;
    }
}
