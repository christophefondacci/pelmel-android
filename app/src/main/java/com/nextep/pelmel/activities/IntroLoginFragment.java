package com.nextep.pelmel.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nextep.pelmel.PelMelApplication;
import com.nextep.pelmel.PelMelConstants;
import com.nextep.pelmel.R;
import com.nextep.pelmel.helpers.PelmelFont;
import com.nextep.pelmel.helpers.Strings;
import com.nextep.pelmel.listeners.UserListener;
import com.nextep.pelmel.model.User;

/**
 * Created by cfondacci on 26/10/15.
 */
public class IntroLoginFragment extends Fragment implements UserListener {

    private TextView loginProgressLabel;
    private Button loginButton;
    private Button registerButton;
    private ProgressBar progressBar;
    private boolean loginProgressShown;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.intro_page_login, container, false);
        loginButton = (Button)view.findViewById(R.id.loginButton);
        registerButton = (Button)view.findViewById(R.id.registerButton);
        loginProgressLabel = (TextView)view.findViewById(R.id.loginProgressLabel);
        progressBar = (ProgressBar)view.findViewById(R.id.loginProgress);
        Strings.setFontFamily(loginProgressLabel, PelmelFont.SOURCE_SANSPRO_LIGHT);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                startActivity(loginIntent);
                getActivity().overridePendingTransition(R.anim.push_left_in,
                        R.anim.push_left_out);
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent registerIntent = new Intent(getActivity(), RegisterActivity.class);
                startActivity(registerIntent);
                getActivity().overridePendingTransition(R.anim.push_left_in,
                        R.anim.push_left_out);
            }
        });
        showLoginProgress(loginProgressShown);
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
            // Retrieving last values
            final SharedPreferences prefs = getActivity().getSharedPreferences(
                    PelMelConstants.PREFS_NAME, 0);
            final String lastUsername = prefs.getString(
                    PelMelConstants.PREF_USERNAME, null);
            final String lastPassword = prefs.getString(
                    PelMelConstants.PREF_PASSWORD, null);
            if (lastUsername != null && lastPassword != null) {
                login(lastUsername, lastPassword);
            }

            // Saving the "intro done" flag
            final SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(PelMelConstants.PREF_INTRODONE, true);
            editor.commit();
        }
    }

    private void login(String username, String password) {
        showLoginProgress(true);
        PelMelApplication.setSearchParentKey(null);
        PelMelApplication.getUserService().login(username, password, this);
    }

    private void showLoginProgress(boolean showProgress) {
        loginProgressShown=showProgress;
        if(loginButton!=null) {
            loginProgressLabel.setText(R.string.loginWaitMsg);
            loginButton.setVisibility(showProgress ? View.INVISIBLE : View.VISIBLE);
            registerButton.setVisibility(showProgress ? View.INVISIBLE : View.VISIBLE);
            loginProgressLabel.setVisibility(showProgress ? View.VISIBLE : View.INVISIBLE);
            progressBar.setVisibility(showProgress ? View.VISIBLE : View.INVISIBLE);
        }
    }
    @Override
    public void userInfoAvailable(User user) {
        PelMelApplication.getUserService().saveLastLoginInfo();

        // Starting the tab
        final Intent mapIntent = new Intent(getActivity(), MainActivity.class); // TabBarActivity.class);
        mapIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Removes other Activities from stack
        startActivity(mapIntent);
        getActivity().finish();
    }

    @Override
    public void userInfoUnavailable() {
        showLoginProgress(false);
    }
}
