package com.nextep.pelmel.model.support;

import android.support.v4.app.Fragment;

import com.nextep.pelmel.activities.MapActivity;
import com.nextep.pelmel.model.CalObject;
import com.nextep.pelmel.providers.SnippetInfoProvider;

/**
 * Created by cfondacci on 21/07/15.
 */
public interface SnippetContainerSupport {

    void showSnippetFor(SnippetInfoProvider provider, boolean isOpen, boolean isRoot);
    void showSnippetFor(CalObject object, boolean isOpen, boolean isRoot);

    /**
     * Shows the given fragment in the snippet
     *
     * @param fragment the fragment to display.
     * @param isOpen whether we should force the snippet to open
     * @param isRoot whether this fragment should be placed at the root of snippet navigation
     */
    void showSnippetForFragment(Fragment fragment, boolean isOpen, boolean isRoot);

    /**
     * Shows the given fragment as a modal dialog
     *
     * @param fragment the fragment to display
     */
    void showDialog(Fragment fragment);

    /**
     * Dismisses any dialog currently displayed. Does nothing if no dialog is currently shown to the
     * user
     */
    void dismissDialog();

    boolean openSnippet();
    boolean minimizeSnippet();

    /**
     * Informs whether current snippet is opened or closed
     *
     * @return <code>true</code> if opened, else <code>false</code>
     */
    boolean isSnippetOpened();

    /**
     * Registeres the current snippet adapter used by the snippet. The container may interact
     * with the adapter when it expands / collapse
     *
     * @param snippetAdapter the current SnippetSectionedAdapter
     */
//    void setSnippetAdapter(SnippetSectionedAdapter snippetAdapter);

    /**
     * Attaches the current child. Must be called on the onAttach() fragment method by child
     * @param childSupport
     */
    void setSnippetChild(SnippetChildSupport childSupport);

    /**
     * Exposes access to the main map fragment
     *
     * @return the Map fragment (mainly for marker interactions)
     */
    MapActivity getMapFragment();

    /**
     * Displays a message as a top banner
     * @param messageResId resource ID of the message to display
     * @param colorRes resource ID of the background color
     * @param timeMs duration of the message, or 0 for unlimited (hideMessage should be called)
     */
    void showMessage(int messageResId, int colorRes, int timeMs);
    void hideMessages();

    /**
     * Workaround activity results limitation with nested fragments by offering children
     * to register themselves for activity result. When registered, the next result will
     * be dispatched to the given fragment and then cleared.
     *
     * @param f the Fragment that should be notified of the next activity result
     */
    void setFragmentForActivityResult(Fragment f);
}
