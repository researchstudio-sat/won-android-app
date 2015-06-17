/*
 * Copyright 2015 Research Studios Austria Forschungsges.m.b.H.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package at.researchstudio.sat.won.android.won_android_app.app.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import at.researchstudio.sat.won.android.won_android_app.app.R;
import at.researchstudio.sat.won.android.won_android_app.app.activity.MainActivity;
import at.researchstudio.sat.won.android.won_android_app.app.event.AuthenticationEvent;
import at.researchstudio.sat.won.android.won_android_app.app.webservice.constants.ResponseCode;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.util.AsyncExecutor;

/**
 * Created by fsuda on 25.08.2014.
 */
public class LoginFragment extends Fragment {
    private static final String LOG_TAG = LoginFragment.class.getSimpleName();

    private EditText mUsername;
    private EditText mPassword;
    private Button mLoginButton;
    private Button mRegisterButton;

    private MainActivity activity;

    //*******FRAGMENT LIFECYCLE************************************************************************
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        mLoginButton = (Button) rootView.findViewById(R.id.login_login);
        mRegisterButton = (Button) rootView.findViewById(R.id.login_register);
        mUsername = (EditText) rootView.findViewById(R.id.login_username);
        mPassword = (EditText) rootView.findViewById(R.id.login_password);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (MainActivity) getActivity();

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.showLoading();
                AsyncExecutor.create().execute(new LoginTask());
            }
        });

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new RegisterFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    //*************************************************************************************************

    private class LoginTask implements AsyncExecutor.RunnableEx {

        @Override
        public void run() throws Exception {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mUsername.getWindowToken(), 0);
            EventBus.getDefault().post(new AuthenticationEvent(activity.getAuthService().login(mUsername.getText().toString(), mPassword.getText().toString())));
        }
    }

    public void onEventMainThread(AuthenticationEvent event) {
        switch(event.getAuthenticationCode()){
            case ResponseCode.LOGIN_SUCCESS:
                activity.showMainMenu();
                break;
            case ResponseCode.LOGIN_NOUSER:
                activity.hideLoading();
                Toast.makeText(activity, activity.getText(R.string.error_login_failed), Toast.LENGTH_LONG).show();
                break;
            case ResponseCode.CONNECTION_ERR:
                activity.hideLoading();
                Toast.makeText(activity, activity.getText(R.string.error_server_not_found), Toast.LENGTH_LONG).show();
                break;
        }
    }
}
