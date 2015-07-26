package com.nextep.pelmel.model.support;

/**
 * A child support optionally implemented by fragments integrated in the snippet. Implementing
 * this method allow them to be notified of snippet events
 * Created by cfondacci on 23/07/15.
 */
public interface SnippetChildSupport {

    /**
     * The snippet has been opened or closed.
     *
     * @param snippetOpened <code>true</code> if the final state is opened, or else <code>false</code>
     */
    void onSnippetOpened(boolean snippetOpened);
}
