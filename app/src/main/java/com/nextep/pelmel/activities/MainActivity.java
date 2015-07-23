package com.nextep.pelmel.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.nextep.pelmel.R;
import com.nextep.pelmel.model.support.SnippetContainerSupport;
import com.nextep.pelmel.providers.SnippetInfoProvider;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class MainActivity extends MainActionBarActivity implements SnippetContainerSupport, SlidingUpPanelLayout.PanelSlideListener {

    private static final String TAG_SNIPPET = "snippet";
    private static final int SNIPPET_HEIGHT = 110;

    private int snippetHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        snippetHeight = getResources().getDimensionPixelSize(R.dimen.snippet_height);
        final SlidingUpPanelLayout slidingLayout = (SlidingUpPanelLayout) findViewById(R.id.slidingPanel);
        final View snippetView = findViewById(R.id.pelmelSnippetContainer);

        slidingLayout.setPanelSlideListener(this);
        slidingLayout.setParalaxOffset(getResources().getDimensionPixelSize(R.dimen.snippet_parallax));
//        slidingLayout.setOverlayed(true);

    }



    @Override
    public void showSnippetFor(SnippetInfoProvider provider, boolean isOpen, boolean isRoot) {
        SnippetListFragment snippetFragment = (SnippetListFragment)getSupportFragmentManager().findFragmentByTag(TAG_SNIPPET);
        final SlidingUpPanelLayout slidingLayout = (SlidingUpPanelLayout) findViewById(R.id.slidingPanel);
        if(snippetFragment == null) {

            // Positioning snippet container
//            View snippetView = findViewById(R.id.pelmelSnippetContainer);
//            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)snippetView.getLayoutParams();
//            snippetHeight = snippetView.getHeight();
//            params.height=snippetHeight;
//            if(!isOpen) {
//                params.topMargin = snippetView.getHeight()-(int)(110f*getResources().getDisplayMetrics().density);
//            } else {
//                params.topMargin = 0;
//            }
//            snippetView.requestLayout();

            snippetFragment = new SnippetListFragment();
            snippetFragment.setInfoProvider(provider);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.pelmelSnippetContainer,snippetFragment,TAG_SNIPPET).addToBackStack(null).commit();
            getSupportFragmentManager().executePendingTransactions();
            slidingLayout.setScrollableView(snippetFragment.getListView());
        }
        slidingLayout.setPanelHeight(snippetHeight);
    }

    @Override
    public boolean openSnippet() {
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

    }

    @Override
    public void onPanelExpanded(View view) {

    }

    @Override
    public void onPanelAnchored(View view) {

    }

    @Override
    public void onPanelHidden(View view) {

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
