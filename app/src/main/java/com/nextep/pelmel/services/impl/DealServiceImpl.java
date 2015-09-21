package com.nextep.pelmel.services.impl;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nextep.json.model.impl.JsonDeal;
import com.nextep.json.model.impl.JsonStatus;
import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.R;
import com.nextep.pelmel.exception.PelmelException;
import com.nextep.pelmel.exception.PelmelWebServiceException;
import com.nextep.pelmel.helpers.ContextHolder;
import com.nextep.pelmel.helpers.Strings;
import com.nextep.pelmel.model.CalObject;
import com.nextep.pelmel.model.Deal;
import com.nextep.pelmel.model.User;
import com.nextep.pelmel.services.DealService;
import com.nextep.pelmel.services.WebService;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cfondacci on 14/09/15.
 */
public class DealServiceImpl implements DealService {
    private static final int PML_DEAL_MIN_REUSE_MILLISECS = 86400000;
    private static final String LOG_TAG = "DEAL";
    private static final String DEAL_USE_ACTION = "/mobileUseDeal";
    private static final String DEAL_REPORT_ACTION = "/mobileReportDeal";
    private WebService webService;
    private ImageView dealsActionView;
    private TextView dealsBadgeView;
    public DealServiceImpl() {
        webService = new WebService();
    }
    @Override
    public String getDealConditionLabel(Deal deal) {
        String label = null;
        if(deal.getLastUsedDate()!=null && (System.currentTimeMillis() - deal.getLastUsedDate().getTime()) < PML_DEAL_MIN_REUSE_MILLISECS) {
            Date nextTime = new Date(deal.getLastUsedDate().getTime()+PML_DEAL_MIN_REUSE_MILLISECS);
            String delay = PelMelApplication.getUiService().getDelayString(nextTime);
            final String template = Strings.getText(R.string.deal_delay);
            label = MessageFormat.format(template,delay.toLowerCase());
        } else if(deal.getMaxUses()>0) {
            String template = Strings.getText(R.string.deal_quota_label);
            label = MessageFormat.format(template,deal.getMaxUses() - deal.getUsedToday(), deal.getMaxUses());
        } else {
            label = Strings.getText(R.string.deal_available);
        }
        return label;
    }

    @Override
    public void useDeal(final Deal deal, final DealListener listener) {

        final Map<String, String> params = new HashMap<>();
        params.put("nxtpUserToken", PelMelApplication.getUserService().getLoggedUser().getToken());
        params.put("dealKey", deal.getKey().toString());

        try {
            final InputStream is = webService.postRequest(new URL(WebService.BASE_URL + DEAL_USE_ACTION), params);
            final Deal resultDeal = getDealFromInputStream(is, deal.getRelatedObject());
            PelMelApplication.runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    listener.dealUsedSuccess(resultDeal);
                }
            });
        } catch (PelmelException | MalformedURLException ex) {
            if (ex instanceof PelmelWebServiceException) {
                PelmelWebServiceException e = (PelmelWebServiceException) ex;
                final InputStream is = e.getResponse();
                final Deal resultDeal = getDealFromInputStream(is, deal.getRelatedObject());
                PelMelApplication.runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.dealUsedFailure(resultDeal);
                    }
                });

            } else {
                Log.e(LOG_TAG, "Internal error: " + ex.getMessage(), ex);
                PelMelApplication.runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.dealUsedFailure(deal);
                    }
                });

            }
        }
    }

    private Deal getDealFromInputStream(InputStream is, CalObject relatedObject) {
        final InputStreamReader reader = new InputStreamReader(is);
        JsonDeal jsonDeal = new Gson().fromJson(reader, new TypeToken<JsonDeal>() {
        }.getType());
        final Deal resultDeal = PelMelApplication.getDataService().getDealFromJson(jsonDeal, relatedObject);
        return resultDeal;
    }

    @Override
    public boolean reportDealProblem(Deal deal) {
        final User user = PelMelApplication.getUserService().getLoggedUser();
        final Map<String, String> params = new HashMap<>();
        params.put("nxtpUserToken", PelMelApplication.getUserService().getLoggedUser().getToken());
        params.put("dealKey", deal.getKey().toString());

        try {
            final InputStream is = webService.postRequest(new URL(WebService.BASE_URL + DEAL_REPORT_ACTION), params);
            final InputStreamReader reader = new InputStreamReader(is);
            final JsonStatus status = new Gson().fromJson(reader, new TypeToken<JsonStatus>() {
            }.getType());
            return status != null && !status.isError();
        } catch (PelmelException | MalformedURLException ex) {
            Log.e(LOG_TAG,"Error while trying to report a problem with the deal");
        }
        return false;
    }

    @Override
    public String getDealTitle(Deal deal) {
        int resIdTitle;
        switch(deal.getDealType()) {
            case TWO_FOR_ONE:
            default:
                resIdTitle = R.string.deal_type_TWO_FOR_ONE;
                break;
        }
        return Strings.getText(resIdTitle);
    }

    @Override
    public void registerDealsViews(ImageView dealsButton, TextView dealsBadgeView) {
        this.dealsActionView = dealsButton;
        this.dealsBadgeView = dealsBadgeView;
        updateDealsBadge();
    }

    public void updateDealsBadge() {
        PelMelApplication.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                final int dealCount = ContextHolder.deals.size();
                if(dealCount > 0) {
                    dealsActionView.setVisibility(View.VISIBLE);
                    dealsBadgeView.setVisibility(View.VISIBLE);
                    dealsBadgeView.setText(String.valueOf(dealCount));
                } else {
                    dealsActionView.setVisibility(View.INVISIBLE);
                    dealsBadgeView.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}
