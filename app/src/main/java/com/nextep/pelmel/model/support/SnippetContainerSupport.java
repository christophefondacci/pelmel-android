package com.nextep.pelmel.model.support;

import android.support.v4.app.Fragment;

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
}
