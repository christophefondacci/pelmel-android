package com.nextep.pelmel.services;

import android.widget.ImageView;
import android.widget.TextView;

import com.nextep.pelmel.model.Deal;

/**
 * Created by cfondacci on 14/09/15.
 */
public interface DealService {

    interface DealListener {
        void dealUsedSuccess(Deal deal);
        void dealUsedFailure(Deal refreshedDeal);
    }
    /**
     * Generates the label of the condition of use for this deal
     *
     * @param deal the deal to generate the condition label for
     * @return the condition label
     */
    String getDealConditionLabel(Deal deal);

    void useDeal(Deal deal, DealListener listener);

    /**
     * Reports a problem with the given deal
     *
     * @param deal the Deal to report a problem on
     * @return <code>true</code> if the report has been made or <code>false</code> if an error occurred
     */
    boolean reportDealProblem(Deal deal);

    /**
     * Returns the deal title of a given deal
     * @param deal the Deal to generate the title for
     * @return the deal title(i.e. the offer)
     */
    String getDealTitle(Deal deal);

    /**
     * Registers the badge view that displays the number of deals available nearby
     *
     * @param dealsButton the button for the list deals action
     * @param dealsBadgeView the TextView hosting the deals badge
     */
    void registerDealsViews(ImageView dealsButton, TextView dealsBadgeView);

    /**
     * Updates the badges based on current deals content
     */
    void updateDealsBadge();
}
