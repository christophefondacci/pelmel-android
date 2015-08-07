package com.nextep.pelmel.adapters;

import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.R;
import com.nextep.pelmel.activities.Refreshable;
import com.nextep.pelmel.dialogs.SelectImageDialogFragment;
import com.nextep.pelmel.helpers.Strings;
import com.nextep.pelmel.listeners.ImageRemovalCallback;
import com.nextep.pelmel.model.CalObject;
import com.nextep.pelmel.model.Image;
import com.nextep.pelmel.model.User;
import com.nextep.pelmel.services.ImageService;

import java.util.List;

/**
 * Created by cfondacci on 04/08/15.
 */
public class ProfilePhotoAdapter extends BaseAdapter implements ImageService.ImageReorderCallback {

    private FragmentActivity activity;
    private Refreshable refreshable;
    private User user;
    private LayoutInflater layoutInflater;

    public ProfilePhotoAdapter(FragmentActivity activity, Refreshable refreshable, User user) {
        this.activity = activity;
        this.refreshable = refreshable;
        this.user = user;
        this.layoutInflater = LayoutInflater.from(activity);
    }

    @Override
    public int getCount() {
        return user.getImages().size()+1;
    }

    @Override
    public Object getItem(int position) {
        return position < user.getImages().size() ? user.getImages().get(position) : null;
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
        return position < user.getImages().size() ? 0 : 1;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final PhotoViewHolder viewHolder;
        final List<Image> photos = user.getImages();
        if(position < photos.size()) {
            if (convertView == null || convertView.getTag() == null) {
                convertView = layoutInflater.inflate(R.layout.list_row_profile_photo, parent, false);

                viewHolder = new PhotoViewHolder();
                viewHolder.deleteButton = (TextView) convertView.findViewById(R.id.deleteButton);
                viewHolder.photoImage = (ImageView) convertView.findViewById(R.id.photoImage);
                viewHolder.photoTitleLabel = (TextView) convertView.findViewById(R.id.photoLabel);
                viewHolder.moveUpButton = (ImageView) convertView.findViewById(R.id.upButton);
                viewHolder.moveDownButton = (ImageView) convertView.findViewById(R.id.downButton);
                viewHolder.confirmDeleteButton = (TextView) convertView.findViewById(R.id.deleteConfirmButton);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (PhotoViewHolder) convertView.getTag();
            }

            // Handling move up/down visibility
            if(position == 0) {
                viewHolder.moveUpButton.setVisibility(View.INVISIBLE);
            }  else {
                viewHolder.moveUpButton.setVisibility(View.VISIBLE);
            }
            if(position == user.getImages().size()-1) {
                viewHolder.moveDownButton.setVisibility(View.INVISIBLE);
            } else {
                viewHolder.moveDownButton.setVisibility(View.VISIBLE);
            }

            // Handling move up/down actions
            viewHolder.moveUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(position>0) {
                        final Image image = photos.get(position);
                        PelMelApplication.getImageService().reorderImage(image, user, position - 1, ProfilePhotoAdapter.this);
                    }
                }
            });
            viewHolder.moveDownButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(position< photos.size()-1) {
                        final Image image = photos.get(position);
                        PelMelApplication.getImageService().reorderImage(image, user, position + 1, ProfilePhotoAdapter.this);
                    }
                }
            });
            viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.confirmDeleteButton.setVisibility(viewHolder.confirmDeleteButton.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
                }
            });
            viewHolder.confirmDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Image image = photos.get(position);
                    viewHolder.photoTitleLabel.setText(R.string.profilePhotoDeleting);
                    PelMelApplication.getImageService().removeImage(image, PelMelApplication.getUserService().getLoggedUser(), PelMelApplication.getUserService().getLoggedUser(), new ImageRemovalCallback() {
                        @Override
                        public void imageRemoved(Image image, CalObject fromObject) {
                            refreshable.updateData();
                            final Toast t = Toast.makeText(activity,
                                    Strings.getText(R.string.photoRemovedSuccess), Toast.LENGTH_LONG);
                            t.show();
                        }
                    });
                }
            });

            Image image = photos.get(position);
            if (image != null) {
                PelMelApplication.getImageService().displayImage(image, true, viewHolder.photoImage);
            }
            if (position == 0) {
                viewHolder.photoTitleLabel.setText(Strings.getText(R.string.profilePhoto));
            } else {
                viewHolder.photoTitleLabel.setText(null);
            }
        } else {
            if(convertView == null || convertView.getTag() == null) {
                convertView = layoutInflater.inflate(R.layout.list_row_add,parent, false);

                final TextView buttonLabel = (TextView)convertView.findViewById(R.id.addButtonLabel);
                buttonLabel.setText(R.string.profileAddPhoto);
                buttonLabel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addPhoto();
                    }
                });
                final TextView addButton = (TextView)convertView.findViewById(R.id.addButton);
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addPhoto();
                    }
                });
            }
        }


        return convertView;
    }

    private void addPhoto() {
        final SelectImageDialogFragment selectDialog = new SelectImageDialogFragment();
        selectDialog.show(activity.getSupportFragmentManager(), "PHOTO");
    }

    @Override
    public void imageReordered(Image image, int oldIndex, int newIndex) {
        refreshable.updateData();
        user.getImages().remove(oldIndex);
        user.getImages().add(newIndex,image);
        final Toast t = Toast.makeText(activity,
                Strings.getText(R.string.photoMovedSuccess), Toast.LENGTH_LONG);
        t.show();
    }

    @Override
    public void imageReorderingFailed(Image image, String reason) {
        final Toast t = Toast.makeText(activity,
                Strings.getText(R.string.photoMovedFailure), Toast.LENGTH_LONG);
        t.show();
    }

    class PhotoViewHolder {
        TextView deleteButton;
        ImageView photoImage;
        TextView photoTitleLabel;
        ImageView moveUpButton;
        ImageView moveDownButton;
        TextView confirmDeleteButton;
    }
}
