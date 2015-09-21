package com.nextep.pelmel.activities;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import com.nextep.pelmel.R;
import com.nextep.pelmel.model.support.SnippetContainerSupport;

/**
 * Created by cfondacci on 17/09/15.
 */
public class WebBrowserFragment extends Activity {

    private WebView webview;
    private String url;
    private SnippetContainerSupport snippetContainerSupport;

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_webbrowser);
        url = getIntent().getStringExtra("URL");
        webview = (WebView)findViewById(R.id.webView);
        webview.loadUrl(url);
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View parentView = inflater.inflate(R.layout.layout_webbrowser,container,false);
//        webview = (WebView)parentView.findViewById(R.id.webView);
//        webview.loadUrl(url);
//        return parentView;
//    }

//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        try {
//            snippetContainerSupport = (SnippetContainerSupport)activity;
//            snippetContainerSupport.setSnippetChild(this);
//        } catch(ClassCastException e) {
//            throw new IllegalStateException("Parent of SnippetListFragment must be a snippetContainerSupport");
//        }
//    }

//    @Override
//    public void onSnippetOpened(boolean snippetOpened) {
//
//    }
//
//    @Override
//    public View getScrollableView() {
//        return null;
//    }
//
//    @Override
//    public void updateData() {
//
//    }
}
