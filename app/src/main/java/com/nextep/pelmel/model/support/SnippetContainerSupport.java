package com.nextep.pelmel.model.support;

import com.nextep.pelmel.providers.SnippetInfoProvider;

/**
 * Created by cfondacci on 21/07/15.
 */
public interface SnippetContainerSupport {

    void showSnippetFor(SnippetInfoProvider provider, boolean isOpen, boolean isRoot);

    boolean openSnippet();
}
