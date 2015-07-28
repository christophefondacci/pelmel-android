package com.nextep.pelmel.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.nextep.pelmel.R;
import com.nextep.pelmel.model.support.SnippetChildSupport;
import com.nextep.pelmel.model.support.SnippetContainerSupport;
import com.nextep.pelmel.providers.SnippetInfoProvider;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class MainActivity extends MainActionBarActivity implements SnippetContainerSupport, SlidingUpPanelLayout.PanelSlideListener {

    private static final String TAG_SNIPPET = "snippet";
    private static final int SNIPPET_HEIGHT = 115;

    private int snippetHeight;
    private SnippetChildSupport snippetChildSupport;
    private boolean snippetOpened = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        snippetHeight = getResources().getDimensionPixelSize(R.dimen.snippet_height);
        final SlidingUpPanelLayout slidingLayout = (SlidingUpPanelLayout) findViewById(R.id.slidingPanel);
        final View snippetView = findViewById(R.id.pelmelSnippetContainer);

        slidingLayout.setPanelSlideListener(this);
        slidingLayout.setParalaxOffset(getResources().getDimensionPixelSize(R.dimen.snippet_parallax));
        slidingLayout.setScrollableView(snippetView);
//        slidingLayout.setPanelHeight(0);
//        slidingLayout.setOverlayed(true);

    }



    @Override
    public void showSnippetFor(SnippetInfoProvider provider, boolean isOpen, boolean isRoot) {
        SnippetListFragment snippetFragment = (SnippetListFragment)getSupportFragmentManager().findFragmentByTag(TAG_SNIPPET);
        final SlidingUpPanelLayout slidingLayout = (SlidingUpPanelLayout) findViewById(R.id.slidingPanel);
        if(snippetFragment == null) {
            snippetFragment = new SnippetListFragment();
            snippetFragment.setInfoProvider(provider);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.pelmelSnippetContainer,snippetFragment,TAG_SNIPPET).addToBackStack(null).commit();
        } else {
            FragmentManager fragmentManager = getSupportFragmentManager();

            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);

            snippetFragment = new SnippetListFragment();
            snippetFragment.setInfoProvider(provider);
            transaction.replace(R.id.pelmelSnippetContainer, snippetFragment,TAG_SNIPPET );
            transaction.addToBackStack(null);
            transaction.commit();
        }
        getSupportFragmentManager().executePendingTransactions();

        slidingLayout.setPanelHeight(snippetHeight);
        notifySnippetOpenState();
    }

    @Override
    public boolean openSnippet() {
        final SlidingUpPanelLayout slidingLayout = (SlidingUpPanelLayout) findViewById(R.id.slidingPanel);
        slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
//        SlidingUpPanelLayout slidingLayout = (SlidingUpPanelLayout) findViewById(R.id.slidingPanel);
//        View mapView = findViewById(R.id.pelmelMap);
//        slidingLayout.setPanelHeight(mapView.getHeight());

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
        slidingLayout.setScrollableView(childSupport.getScrollableView());
        notifySnippetOpenState();
    }

    @Override
    public boolean isSnippetOpened() {
        return snippetOpened;
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
