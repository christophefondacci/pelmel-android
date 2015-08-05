package com.nextep.pelmel.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.R;
import com.nextep.pelmel.adapters.ProfileDescriptionAdapter;
import com.nextep.pelmel.adapters.ProfileHeaderAdapter;
import com.nextep.pelmel.adapters.ProfilePhotoAdapter;
import com.nextep.pelmel.adapters.ProfileSectionedAdapter;
import com.nextep.pelmel.adapters.ProfileTagAdapter;
import com.nextep.pelmel.listeners.ImageUploadCallback;
import com.nextep.pelmel.listeners.UserListener;
import com.nextep.pelmel.model.CalObject;
import com.nextep.pelmel.model.Image;
import com.nextep.pelmel.model.Tag;
import com.nextep.pelmel.model.User;
import com.nextep.pelmel.services.ImageService;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by cfondacci on 04/08/15.
 */
public class MyProfileActivity extends ActionBarActivity implements UserListener,ImageUploadCallback, Refreshable, AdapterView.OnItemClickListener {


    private ListView listview;
    private ProgressDialog progressDialog;
    private ImageService imageService;
    private User user;
    private ProfileSectionedAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myprofile);
        listview = (ListView)findViewById(R.id.listView);
        listview.setOnItemClickListener(this);
        imageService = PelMelApplication.getImageService();
        PelMelApplication.getUserService().getCurrentUser(this);
    }

    @Override
    public void userInfoAvailable(User user) {
        this.user = user;
        adapter = new ProfileSectionedAdapter(this);

        adapter.addSection(ProfileSectionedAdapter.SECTION_HEADER,new ProfileHeaderAdapter(this,this,user));
        adapter.addSection(ProfileSectionedAdapter.SECTION_PHOTOS,new ProfilePhotoAdapter(this,this,user));
        adapter.addSection(ProfileSectionedAdapter.SECTION_DESCRIPTIONS,new ProfileDescriptionAdapter(this,user));
        adapter.addSection(ProfileSectionedAdapter.SECTION_TAGS,new ProfileTagAdapter(this,user));

        listview.setAdapter(adapter);
    }

    @Override
    public void userInfoUnavailable() {
        this.user = null;
    }

    @Override
    public void updateData() {
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("photo", "Result is " + requestCode + " : result code "
                + resultCode);
        // If user cancelled the photo upload, then data will be null
        if (data != null && data.getData() != null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(getString(R.string.upload_wait_message));
            progressDialog.setTitle(getString(R.string.waitTitle));
            progressDialog.setIndeterminate(true);
            progressDialog.show();

            final Uri selectedImage = data.getData();
            final File f = imageService.getOrientedImageFileFromUri(this,
                    selectedImage);
            imageService.uploadImage(f, user, user, this);
        }

    }

    @Override
    public void imageUploaded(Image image, CalObject parent) {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        if (user != null) {
            updateData();
            final Toast t = Toast.makeText(getBaseContext(),
                    getText(R.string.photoUploadSuccess), Toast.LENGTH_LONG);
            t.show();
        }

    }

    @Override
    public void imageUploadFailed() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        final Toast t = Toast.makeText(getBaseContext(),
                getText(R.string.photoUploadFailed), Toast.LENGTH_LONG);
        t.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.account_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:

                progressDialog = new ProgressDialog(this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage(getString(R.string.save_wait_message));
                progressDialog.setTitle(getString(R.string.waitTitle));
                progressDialog.setIndeterminate(true);
                try {
                    progressDialog.show();
                } catch (final Exception e) {
                    Log.e("ACCOUNT",
                            "Error while showing dialog : " + e.getMessage(), e);
                }
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        PelMelApplication.getDataService().saveProfile(
                                PelMelApplication.getUserService().getLoggedUser(),
                                PelMelApplication.getLocalizationService().getLocation()
                                        .getLatitude(),
                                PelMelApplication.getLocalizationService().getLocation()
                                        .getLongitude());
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        progressDialog.dismiss();
                        progressDialog = null;
                        final Intent intent = new Intent(MyProfileActivity.this,MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(intent);

                    }
                }.execute();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final Object item = listview.getAdapter().getItem(position);
        if(item instanceof Tag) {
            final Tag clickedTag = (Tag)item;
            // This is a toggle action, so if the tag is already assigned we remove it, otherwise we add it
            boolean tagRemoved = false;
            // Browsing all user tags
            for(Tag t : new ArrayList<>(user.getTags())) {
                // If found, we remove it
                if(t.getCode().equals(clickedTag.getCode())) {
                    user.removeTag(t);
                    tagRemoved = true;
                    break;
                }
            }
            // If not removed, then we add it
            if(!tagRemoved) {
                user.addTag(clickedTag);
            }

            final ProfileTagAdapter tagAdapter = (ProfileTagAdapter)adapter.getSection(ProfileSectionedAdapter.SECTION_TAGS);
            tagAdapter.invalidate();
            updateData();
        }
    }
}
