package com.nextep.pelmel.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.nextep.pelmel.R;
import com.nextep.pelmel.model.support.SnippetChildSupport;
import com.nextep.pelmel.model.support.SnippetContainerSupport;

/**
 * Created by cfondacci on 17/09/15.
 */
public class WebBrowserFragment extends android.support.v4.app.Fragment implements SnippetChildSupport {

    private WebView webview;
    private String url;
    private SnippetContainerSupport snippetContainerSupport;

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_webbrowser,container,false);
        webview = (WebView)view.findViewById(R.id.webView);
        webview.loadUrl(url);
        snippetContainerSupport.setSnippetChild(this);
        return view;
    }

    @Override
    public void onSnippetOpened(boolean snippetOpened) {

    }

    @Override
    public View getScrollableView() {
        return null;
    }

    @Override
    public void updateData() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        snippetContainerSupport = (SnippetContainerSupport)activity;

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
