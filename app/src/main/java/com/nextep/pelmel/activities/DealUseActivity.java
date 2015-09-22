package com.nextep.pelmel.activities;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.PelMelConstants;
import com.nextep.pelmel.R;
import com.nextep.pelmel.helpers.PelmelTimer;
import com.nextep.pelmel.helpers.Strings;
import com.nextep.pelmel.listeners.UserListener;
import com.nextep.pelmel.model.Deal;
import com.nextep.pelmel.model.User;
import com.nextep.pelmel.model.support.SnippetChildSupport;
import com.nextep.pelmel.model.support.SnippetContainerSupport;
import com.nextep.pelmel.services.DealService;
import com.nextep.pelmel.services.WebService;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by cfondacci on 15/09/15.
 */
public class DealUseActivity extends Fragment implements UserListener, DealService.DealListener, SnippetChildSupport {

    private static final String LOG_TAG = "DEAL";
    private static final String URL_HELP = "/deal-help";

    private TextView dealTitleLabel;
    private TextView presentLabel;
    private ImageView orangeCircle;
    private ImageView centerCircle;
    private ImageView grayCircle;
    private View greenOverlay;
    private Button dealButton;
    private Button dismissButton;
    private Button reportButton;
    private Button helpButton;
    private ImageView userThumbImage;
    private TextView userNicknameLabel;
    private TextView legalLabel;
    private Map<View,Float> anglesMap = new HashMap<>();
    private Map<View,ScaleAnimation> scaleAnimationsMap = new HashMap<>();
    private Map<View,RotateAnimation> rotateAnimationsMap = new HashMap<>();
    private boolean destroyed;
    private boolean buttonPressed;
    private Deal deal;
    private PelmelTimer pressTimer;
    private SnippetContainerSupport snippetContainerSupport;

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

    private class DealTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            float stdWidth = PelMelApplication.getInstance().getResources().getDimension(R.dimen.deal_animation_circle_size);
//                orangeCircle.measure(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
//                float currentScale = ((float)orangeCircle.getMeasuredWidth())/stdWidth;
            if((event.getActionMasked() & MotionEvent.ACTION_DOWN) == MotionEvent.ACTION_DOWN && !buttonPressed) {
                Log.d(LOG_TAG, "DOWN");
                buttonPressed = true;
                dealTapped(1.0f, 1.3f);
                updateCountdown(2000);
                pressTimer = new PelmelTimer(2000,1000) {
                    @Override
                    public void onTick(final long millisUntilFinished) {
                        Log.i("DEAL","Tick " + millisUntilFinished);
                        PelMelApplication.runOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                updateCountdown(millisUntilFinished);
                            }
                        });
                    }

                    @Override
                    public void onFinish() {
                        Log.i(LOG_TAG,"FINISHED");
                        if(buttonPressed) {
                            dealButton.setOnTouchListener(null);
                            PelMelApplication.runOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    presentLabel.setText(Strings.getText(R.string.deal_activation_message));
                                }
                            });
                            new AsyncTask<Void,Void,Void>() {

                                @Override
                                protected Void doInBackground(Void... params) {
                                    PelMelApplication.getDealService().useDeal(deal, DealUseActivity.this);
                                    return null;
                                }
                            }.execute();

                        }
                    }
                }.start();

            } else if((event.getActionMasked() & MotionEvent.ACTION_UP) == MotionEvent.ACTION_UP) {
                Log.d(LOG_TAG, "UP");
                buttonPressed = false;
                if(pressTimer != null) {
                    pressTimer.cancel();
                }
                updatePresentLabel();
                animate(orangeCircle, 2000);
                animate(grayCircle, 1000);
                dealTapped(1.3f,1.0f);
            }
            return true;
        }
    }
    public void setDeal(Deal deal) {
        this.deal = deal;
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            snippetContainerSupport = (SnippetContainerSupport)activity;
        } catch(ClassCastException e) {
            throw new IllegalStateException("Parent of SnippetListFragment must be a snippetContainerSupport");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.layout_use_deal,container,false);

        // Getting controls
        dealTitleLabel = (TextView)view.findViewById(R.id.dealTitle);
        presentLabel = (TextView)view.findViewById(R.id.presentLabel);
        orangeCircle = (ImageView)view.findViewById(R.id.dealCircleOrange);
        grayCircle = (ImageView)view.findViewById(R.id.dealCircleGray);
        centerCircle = (ImageView)view.findViewById(R.id.dealCircleCenter);
        greenOverlay = (View)view.findViewById(R.id.greenOverlay);
        dealButton = (Button)view.findViewById(R.id.dealButton);
        userThumbImage = (ImageView)view.findViewById(R.id.userThumbImage);
        userNicknameLabel = (TextView)view.findViewById(R.id.userNicknameLabel);
        legalLabel = (TextView)view.findViewById(R.id.legalLabel);
        dismissButton = (Button)view.findViewById(R.id.dismissButton);
        reportButton = (Button)view.findViewById(R.id.reportButton);
        helpButton = (Button)view.findViewById(R.id.helpButton);

        final String legalTemplate = Strings.getText(R.string.deal_use_legal);
        final String legalText = MessageFormat.format(legalTemplate,deal.getRelatedObject().getName());
        legalLabel.setText(legalText);
        updatePresentLabel();

        dealButton.setOnTouchListener(new DealTouchListener());
        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DealUseActivity.this.getActivity().onBackPressed();
            }
        });
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AsyncTask<Void,Void,Boolean>() {
                    @Override
                    protected Boolean doInBackground(Void... params) {
                        final boolean success = PelMelApplication.getDealService().reportDealProblem(deal);
                        return success;
                    }

                    @Override
                    protected void onPostExecute(Boolean success) {
                        if(success) {
                            PelMelApplication.getUiService().showInfoMessage(DealUseActivity.this.getActivity(),R.string.deal_report_title,R.string.deal_report_text);
                        } else {
                            PelMelApplication.getUiService().showInfoMessage(DealUseActivity.this.getActivity(),R.string.deal_report_title,R.string.deal_report_error_text);
                        }
                        dismissButton.setVisibility(View.VISIBLE);
                    }
                }.execute();

            }
        });

        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                WebBrowserFragment fragment = new WebBrowserFragment();
                fragment.setUrl(WebService.BASE_URL + URL_HELP);
                snippetContainerSupport.showSnippetForFragment(fragment, true, false);
//                final Intent intent = new Intent(getActivity(),WebBrowserFragment.class);
//                intent.putExtra("URL",WebService.BASE_URL + URL_HELP);
//                startActivity(intent);
//                WebBrowserFragment fragment = new WebBrowserFragment();
//                fragment.setUrl(WebService.BASE_URL + URL_HELP);
//                snippetContainerSupport.showSnippetForFragment(fragment,true,false);
            }
        });
        animate(orangeCircle, 2000);
        animate(grayCircle,1000);
        PelMelApplication.getUserService().getCurrentUser(this);
        snippetContainerSupport.setSnippetChild(this);
        return view;
    }

    private void updateCountdown(long millis) {
        final String template = Strings.getText(R.string.deal_use_countdown);
        final String countdownLabel = MessageFormat.format(template,Math.ceil((float) millis / 1000.0f));
        presentLabel.setText(countdownLabel);
    }

    private void updatePresentLabel() {
        presentLabel.setText(Strings.getText(R.string.deal_present));
    }

    private void dealTapped(float startScale, float targetScale)  {
        greenOverlay.setVisibility(View.VISIBLE);
        final Animation anim = new AlphaAnimation(targetScale>1 ? 0.0f : 0.9f, targetScale > 1 ? 0.9f : 0);
        anim.setDuration(2000);
        anim.setFillAfter(true);
        greenOverlay.startAnimation(anim);

        float center = PelMelApplication.getInstance().getResources().getDimension(R.dimen.deal_animation_circle_size)/2.0f;

        final ScaleAnimation scaleAnim1 = new ScaleAnimation(startScale,targetScale,startScale,targetScale,center,center);
        final ScaleAnimation scaleAnim2 = new ScaleAnimation(startScale,targetScale,startScale,targetScale,center,center);
        final ScaleAnimation scaleAnim3 = new ScaleAnimation(startScale,targetScale,startScale,targetScale,center,center);

        scaleAnim1.setDuration(500);
        scaleAnim2.setDuration(500);
        scaleAnim3.setDuration(500);
        scaleAnim1.setFillAfter(true);
        scaleAnim2.setFillAfter(true);
        scaleAnim3.setFillAfter(true);

        final AnimationSet set1 = new AnimationSet(false);
        final AnimationSet set2 = new AnimationSet(false);
        final AnimationSet set3 = new AnimationSet(false);
        set1.setFillAfter(true);
        set2.setFillAfter(true);
        set3.setFillAfter(true);
        set1.addAnimation(scaleAnim1);
        set2.addAnimation(scaleAnim2);
        set3.addAnimation(scaleAnim3);
        scaleAnimationsMap.put(orangeCircle, scaleAnim1);
        scaleAnimationsMap.put(grayCircle, scaleAnim2);
        scaleAnimationsMap.put(centerCircle,scaleAnim3);
        final RotateAnimation rot1 = rotateAnimationsMap.get(orangeCircle);
        final RotateAnimation rot2 = rotateAnimationsMap.get(grayCircle);
        final RotateAnimation rot3 = rotateAnimationsMap.get(centerCircle);
        if(rot1!=null) {
            set1.addAnimation(rot1);
        }
        if(rot2!=null) {
            set2.addAnimation(rot2);
        }
        if(rot3!=null) {
            set3.addAnimation(rot3);
        }


        orangeCircle.startAnimation(set1);
        grayCircle.startAnimation(set2);
        centerCircle.startAnimation(set3);
    }

    @Override
    public void onDestroy() {
        destroyed = true;
        super.onDestroy();
    }

    private void animate(final View view, final int duration) {
        Float previousAngle = anglesMap.get(view);
        if(previousAngle == null) {
            previousAngle = (float)0;
        }
        float angle = previousAngle + new Random().nextFloat()*180-90;
        anglesMap.put(view,angle);
        float center = PelMelApplication.getInstance().getResources().getDimension(R.dimen.deal_animation_circle_size)/2.0f;
        final RotateAnimation rotateAnimation = new RotateAnimation(previousAngle,angle,center,center);
        rotateAnimation.setDuration(duration);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (!destroyed && !buttonPressed) {
                    animate(view, duration);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        final AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(rotateAnimation);
        rotateAnimationsMap.put(view,rotateAnimation);
        final ScaleAnimation scaleAnimation = scaleAnimationsMap.get(view);
        if(scaleAnimation!=null && !scaleAnimation.hasEnded()) {
            animationSet.addAnimation(scaleAnimation);
        }
        view.startAnimation(animationSet);
    }

    @Override
    public void userInfoAvailable(User user) {
        if(user.getThumb()!=null) {
            PelMelApplication.getImageService().displayImage(user.getThumb(), false, userThumbImage);
        }
        userNicknameLabel.setText(user.getName());
    }

    @Override
    public void userInfoUnavailable() {

    }

    private void animateMinimize(View view) {
        float center = PelMelApplication.getInstance().getResources().getDimension(R.dimen.deal_animation_circle_size)/2.0f;
        ScaleAnimation animation = new ScaleAnimation(1.0f,0.0f, 1.0f,0.0f,center,center);
        animation.setFillAfter(true);
        animation.setDuration(500);
        view.startAnimation(animation);
    }
    @Override
    public void dealUsedSuccess(Deal deal) {
        animateMinimize(orangeCircle);
        animateMinimize(centerCircle);
        animateMinimize(grayCircle);
        presentLabel.setText(Strings.getText(R.string.deal_proceed));
        dismissButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void dealUsedFailure(Deal refreshedDeal) {
        animateMinimize(orangeCircle);
        animateMinimize(centerCircle);
        animateMinimize(grayCircle);
        greenOverlay.setBackgroundResource(R.drawable.circle_red);
        greenOverlay.setVisibility(View.VISIBLE);
        if(refreshedDeal.getLastUsedDate().getTime()>System.currentTimeMillis()- PelMelConstants.DEAL_USE_TIME_MILLIS) {
            presentLabel.setText(Strings.getText(R.string.deal_error_alreadyUsed));
        } else if(refreshedDeal.getUsedToday()>= refreshedDeal.getMaxUses()) {
            presentLabel.setText(Strings.getText(R.string.deal_error_quotaReached));
        } else {
            presentLabel.setText(Strings.getText(R.string.deal_error_generic));
        }
        dismissButton.setVisibility(View.VISIBLE);
    }
}
