package com.nextep.pelmel.adapters;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.R;
import com.nextep.pelmel.activities.Refreshable;
import com.nextep.pelmel.dialogs.SelectImageDialogFragment;
import com.nextep.pelmel.model.User;

import java.text.DateFormat;
import java.util.Calendar;

/**
 * Created by cfondacci on 04/08/15.
 */
public class ProfileHeaderAdapter extends BaseAdapter {

    private FragmentActivity activity;
    private User user;
    private LayoutInflater layoutInflater;
    private DateFormat birthdateFormatter = DateFormat.getDateInstance(DateFormat.SHORT);
    private Refreshable refreshable;

    public ProfileHeaderAdapter(FragmentActivity activity, Refreshable refreshable, User user ) {
        this.activity = activity;
        this.user = user;
        this.layoutInflater = LayoutInflater.from(activity);
        this.refreshable = refreshable;
    }


    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Object getItem(int position) {
        return position;
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
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(position == 0) {
            final ProfileThumbViewHolder viewHolder;
            if(convertView == null || convertView.getTag() == null) {
                convertView = layoutInflater.inflate(R.layout.list_row_profile_thumb,parent,false);

                viewHolder = new ProfileThumbViewHolder();
                viewHolder.thumbImageView = (ImageView)convertView.findViewById(R.id.thumbImage);
                viewHolder.nicknameText = (TextView)convertView.findViewById(R.id.nicknameText);
                viewHolder.editButton = (ImageView)convertView.findViewById(R.id.editButton);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ProfileThumbViewHolder)convertView.getTag();
            }

            // Loading thumb
            if(user.getThumb() != null) {
                PelMelApplication.getImageService().displayImage(user.getThumb(),false,viewHolder.thumbImageView);
            }
            // Updating nickname
            viewHolder.nicknameText.setText(user.getName());
            // Actions
            viewHolder.nicknameText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editNickname(viewHolder.nicknameText);
                }
            });
            viewHolder.editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editNickname(viewHolder.nicknameText);
                }
            });

            viewHolder.thumbImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final SelectImageDialogFragment selectDialog = new SelectImageDialogFragment();
                    selectDialog.show(activity.getSupportFragmentManager(), "PHOTO");
                }
            });

        } else {
            final BirthdayViewHolder viewHolder;
            if(convertView == null || convertView.getTag() == null) {
                convertView = layoutInflater.inflate(R.layout.list_row_profile_birthday, parent, false);

                viewHolder = new BirthdayViewHolder();
                viewHolder.birthdayLabel = (TextView)convertView.findViewById(R.id.birthDateLabel);
                viewHolder.birthdayValueLabel = (TextView)convertView.findViewById(R.id.birthDateValueLabel);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (BirthdayViewHolder)convertView.getTag();
            }

            // Setting label
            if(user.getBirthDate() != null) {
                viewHolder.birthdayValueLabel.setText(birthdateFormatter.format(user.getBirthDate()));
            } else {
                viewHolder.birthdayValueLabel.setText("-");
            }

            // Tapping lines
            viewHolder.birthdayLabel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editBirthday();
                }
            });
            viewHolder.birthdayValueLabel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editBirthday();
                }
            });

        }
        return convertView;
    }

    private void editBirthday() {
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setUser(user);
        fragment.setRefreshable(refreshable);
        fragment.show(activity.getSupportFragmentManager(),"datePicker");
    }

    private void editNickname(TextView targetTextView) {
        // Prompt for nickname
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.profileNicknameInputTitle);

        final EditText nicknameEdit = new EditText(activity);
        nicknameEdit.setInputType(InputType.TYPE_CLASS_TEXT);
        nicknameEdit.setText(user.getName());
        nicknameEdit.setSingleLine(true);
        builder.setView(nicknameEdit);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                user.setName(nicknameEdit.getText().toString());
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

    class ProfileThumbViewHolder {
        ImageView thumbImageView;
        TextView nicknameText;
        ImageView editButton;
    }
    class BirthdayViewHolder {
        TextView birthdayLabel;
        TextView birthdayValueLabel;
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        private int day,month,year;
        private Context context;
        private User user;
        private Refreshable refreshable;

        public void setUser(User user) {
            Calendar c = Calendar.getInstance();
            c.setTime(user.getBirthDate());
            this.day = c.get(Calendar.DAY_OF_MONTH);
            this.month = c.get(Calendar.MONTH);
            this.year = c.get(Calendar.YEAR);
            this.user = user;
        }

        public void setRefreshable(Refreshable refreshable) {
            this.refreshable = refreshable;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new DatePickerDialog(this.getActivity(),this,year,month,day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR,year);
            c.set(Calendar.MONTH,monthOfYear);
            c.set(Calendar.DAY_OF_MONTH,dayOfMonth);
            user.setBirthDate(c.getTime());
            refreshable.updateData();
        }
    }
}
