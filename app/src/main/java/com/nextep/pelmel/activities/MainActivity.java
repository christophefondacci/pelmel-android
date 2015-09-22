package com.nextep.pelmel.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.PelMelConstants;
import com.nextep.pelmel.R;
import com.nextep.pelmel.listeners.ImageUploadCallback;
import com.nextep.pelmel.model.CalObject;
import com.nextep.pelmel.model.Image;
import com.nextep.pelmel.model.support.SnippetChildSupport;
import com.nextep.pelmel.model.support.SnippetContainerSupport;
import com.nextep.pelmel.providers.SnippetInfoProvider;
import com.nextep.pelmel.views.SlidingUpPanelLayout;

import java.io.File;

public class MainActivity extends MainActionBarActivity implements SnippetContainerSupport, SlidingUpPanelLayout.PanelSlideListener,ImageUploadCallback {

    private static final String TAG_SNIPPET = "snippet";
    private static final String TAG_DIALOG = "dialog";
    private static final int SNIPPET_HEIGHT = 115;

    private int snippetHeight;
    private SnippetChildSupport snippetChildSupport;
    private boolean snippetOpened = false;
    private MapActivity mapFragment;
    private TextView bannerText;
    private View bannerContainer;
    private ProgressBar bannerProgress;
    private ProgressDialog progressDialog;

    private View slidingDragView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        snippetHeight = getResources().getDimensionPixelSize(R.dimen.snippet_height);
        final SlidingUpPanelLayout slidingLayout = (SlidingUpPanelLayout) findViewById(R.id.slidingPanel);
        final View snippetView = findViewById(R.id.pelmelSnippetContainer);
        bannerText = (TextView)findViewById(R.id.bannerText);
        bannerContainer = findViewById(R.id.bannerContainer);
        bannerProgress = (ProgressBar)findViewById(R.id.bannerProgress);

        slidingLayout.setPanelSlideListener(this);
        slidingLayout.setParalaxOffset(getResources().getDimensionPixelSize(R.dimen.snippet_parallax));
        slidingLayout.setScrollableView(snippetView);

        PelMelApplication.setSnippetContainerSupport(this);

        getSupportActionBar().setIcon(R.drawable.pelmel_icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mapFragment = (MapActivity)getSupportFragmentManager().findFragmentById(R.id.pelmelMap);


//        slidingLayout.setPanelHeight(0);
//        slidingLayout.setOverlayed(true);

    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        if(getIntent().getBooleanExtra(PelMelConstants.INTENT_PARAM_SHOW_MESSAGES,false)) {
            Fragment fragment = new ChatActivity();
            showSnippetForFragment(fragment,true,true);
        }
    }

    @Override
    public void showSnippetFor(SnippetInfoProvider provider, boolean isOpen, boolean isRoot) {
        final SnippetListFragment snippetFragment = new SnippetListFragment();
        snippetFragment.setInfoProvider(provider);
        showSnippetForFragment(snippetFragment, isOpen, isRoot);

    }

    @Override
    public void showSnippetForFragment(Fragment fragment, boolean isOpen, boolean isRoot) {
        final SlidingUpPanelLayout slidingLayout = (SlidingUpPanelLayout) findViewById(R.id.slidingPanel);
        Fragment snippetFragment = getSupportFragmentManager().findFragmentByTag(TAG_SNIPPET);
        if(snippetFragment == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.pelmelSnippetContainer,fragment,TAG_SNIPPET).commit();
        } else {
            FragmentManager fragmentManager = getSupportFragmentManager();

            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
            transaction.replace(R.id.pelmelSnippetContainer, fragment,TAG_SNIPPET );
            transaction.addToBackStack(null);
            transaction.commit();
        }
        getSupportFragmentManager().executePendingTransactions();

        try {
            slidingLayout.setPanelHeight(snippetHeight);
            if (isOpen) {
                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
            notifySnippetOpenState();
        } catch(RuntimeException e) {
            Log.e(TAG_SNIPPET,"Unable to set snippet height for now: " + e.getMessage(),e);
        }

    }

    @Override
    public boolean openSnippet() {
        final SlidingUpPanelLayout slidingLayout = (SlidingUpPanelLayout) findViewById(R.id.slidingPanel);
        slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        return true;
    }

    @Override
    public boolean minimizeSnippet() {
        final SlidingUpPanelLayout slidingLayout = (SlidingUpPanelLayout) findViewById(R.id.slidingPanel);
        slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

        return true;
    }

    @Override
    public void onPanelSlide(View view, float v) {

    }

    @Override
    public void onPanelCollapsed(View view) {
        setSnippetOpened(false);
    }

    @Override
    public void onPanelExpanded(View view) {
        setSnippetOpened(true);
    }

    private void setSnippetOpened(boolean opened) {
        if(opened != snippetOpened) {
            snippetOpened = opened;
            notifySnippetOpenState();
        }
    }

    /**
     * Notifies the current snippet adapter that the snippet is opened
     */
    private void notifySnippetOpenState() {
        if (snippetChildSupport != null) {
            snippetChildSupport.onSnippetOpened(snippetOpened);
        }
    }
    @Override
    public void onPanelAnchored(View view) {

    }

    @Override
    public void onPanelHidden(View view) {

    }

    @Override
    public void setSnippetChild(SnippetChildSupport childSupport) {
        this.snippetChildSupport=childSupport;
        final SlidingUpPanelLayout slidingLayout = (SlidingUpPanelLayout) findViewById(R.id.slidingPanel);
        if(childSupport != null) {
            slidingLayout.setScrollableView(childSupport.getScrollableView());
            if(childSupport.getScrollableView()==null) {
                slidingLayout.setSlidingEnabled(false);
//                slidingLayout.setEnabled(false);
            } else {
                slidingLayout.setSlidingEnabled(true);
//                slidingLayout.setEnabled(true);

            }
//                slidingLayout.setDragView(null);
//            } else {
//                slidingLayout.setTouchEnabled(true);
//            }
        }
        notifySnippetOpenState();
    }

    @Override
    public boolean isSnippetOpened() {
        return snippetOpened;
    }

    @Override
    public void showSnippetFor(CalObject object, boolean isOpen, boolean isRoot) {
        final SnippetInfoProvider infoProvider = PelMelApplication.getUiService().buildInfoProviderFor(object);
        showSnippetFor(infoProvider,isOpen,isRoot);
    }

    @Override
    public void showDialog(Fragment fragment) {

        final FragmentManager mgr = getSupportFragmentManager();
        final FragmentTransaction transaction = mgr.beginTransaction();
        Fragment dialogFragment = getSupportFragmentManager().findFragmentByTag(TAG_DIALOG);
        if(dialogFragment!=null) {
            transaction.remove(dialogFragment);
        }

        final View dialogShade = findViewById(R.id.dialogShade);
        dialogShade.setVisibility(View.VISIBLE);

        final RelativeLayout dialogContainer = (RelativeLayout) findViewById(R.id.dialogContainer);
//        transaction.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out);
        transaction.add(R.id.dialogContainer, fragment, TAG_DIALOG);
        transaction.addToBackStack(null);
        transaction.commit();


    }

    @Override
    public void dismissDialog() {
        final FragmentManager mgr = getSupportFragmentManager();
        final FragmentTransaction transaction = mgr.beginTransaction();
        Fragment dialogFragment = getSupportFragmentManager().findFragmentByTag(TAG_DIALOG);
        if(dialogFragment!=null) {
            transaction.remove(dialogFragment);
        }
        final View dialogShade = findViewById(R.id.dialogShade);
        dialogShade.setVisibility(View.INVISIBLE);
        transaction.commit();

    }

    @Override
    public MapActivity getMapFragment() {
        return mapFragment;
    }

    @Override
    public void showMessage(int messageResId, int colorRes, int timeMs) {
        bannerContainer.setVisibility(View.VISIBLE);
        bannerContainer.setBackgroundResource(colorRes);
        bannerText.setText(messageResId);
    }

    @Override
    public void hideMessages() {
        bannerContainer.setVisibility(View.INVISIBLE);
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
            final File f = PelMelApplication.getImageService().getOrientedImageFileFromUri(this,
                    selectedImage);
            PelMelApplication.getImageService().uploadImage(f, PelMelApplication.getOverviewObject(), PelMelApplication.getUserService().getLoggedUser(), this);
        }

    }

    @Override
    public void imageUploaded(Image image, CalObject parent) {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        if(snippetChildSupport != null) {
            snippetChildSupport.updateData();
        }
        final Toast t = Toast.makeText(getBaseContext(),
                getText(R.string.photoUploadSuccess), Toast.LENGTH_LONG);
        t.show();

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
    //    @Override
//    public boolean onChildTouch(View v, MotionEvent e) {
//        final View snippetContainer = findViewById(R.id.pelmelSnippetContainer);
//        final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)snippetContainer.getLayoutParams();
//        if(params.topMargin>-params.height) {
//            if (e.getAction() == MotionEvent.ACTION_DOWN) {
//                startBottomOffset = params.topMargin;
//                startY = e.getY();
//            } else if (e.getAction() == MotionEvent.ACTION_MOVE) {
//                float delta = e.getY() - startBottomOffset;
//                if(delta>5) {
//                    params.topMargin = (int) (startBottomOffset + delta);
////                snippetContainer.setLayoutParams(params);
//                    snippetContainer.requestLayout();
//                }
//            }
//            return true;
//        } else {
//            return true;
//        }
//    }
}
