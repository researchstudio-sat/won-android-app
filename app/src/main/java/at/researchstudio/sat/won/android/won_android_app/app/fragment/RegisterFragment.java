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
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import at.researchstudio.sat.won.android.won_android_app.app.R;
import at.researchstudio.sat.won.android.won_android_app.app.activity.MainActivity;
import at.researchstudio.sat.won.android.won_android_app.app.event.AuthenticationEvent;
import at.researchstudio.sat.won.android.won_android_app.app.util.StringUtils;
import at.researchstudio.sat.won.android.won_android_app.app.webservice.constants.ResponseCode;
import at.researchstudio.sat.won.android.won_android_app.app.webservice.model.User;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.util.AsyncExecutor;

/**
 * Created by fsuda on 25.08.2014.
 */
public class RegisterFragment extends Fragment {
    private static final String LOG_TAG = RegisterFragment.class.getSimpleName();

    private TextView mErrorText;
    private EditText mUsername;
    private EditText mPassword;
    private EditText mRepeatPassword;
    private Button mRegisterButton;
    private Button mBackButton;

    private MainActivity activity;

    //*******FRAGMENT LIFECYCLE************************************************************************
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_register, container, false);

        mRegisterButton = (Button) rootView.findViewById(R.id.register_register);
        mBackButton = (Button) rootView.findViewById(R.id.register_back);
        mUsername = (EditText) rootView.findViewById(R.id.register_username);
        mPassword = (EditText) rootView.findViewById(R.id.register_password);
        mRepeatPassword = (EditText) rootView.findViewById(R.id.register_repeatpassword);

        mErrorText = (TextView) rootView.findViewById(R.id.register_error);

        return rootView;
    }

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
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

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mErrorText.setVisibility(View.GONE);
                Log.d(LOG_TAG,"PW: "+mPassword.getText());
                Log.d(LOG_TAG,"PR: "+mRepeatPassword.getText());

                if(!StringUtils.isEmail(mUsername.getText())){
                    mErrorText.setText(activity.getString(R.string.error_register_email_invalid));
                    mErrorText.setVisibility(View.VISIBLE);
                }else if(mPassword.getText().length()<6){
                    mErrorText.setText(activity.getString(R.string.error_register_pw_short));
                    mErrorText.setVisibility(View.VISIBLE);
                }else if(!(mPassword.getText().toString().equals(mRepeatPassword.getText().toString()))) {
                    mErrorText.setText(activity.getString(R.string.error_register_pw_notequal));
                    mErrorText.setVisibility(View.VISIBLE);
                }else{
                    activity.showLoading();
                    mErrorText.setVisibility(View.GONE);
                    AsyncExecutor.create().execute(new RegisterTask());
                }
            }
        });

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.popBackStackIfPossible();
            }
        });
    }

    //*************************************************************************************************

    private class RegisterTask implements AsyncExecutor.RunnableEx {

        @Override
        public void run() throws Exception {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mUsername.getWindowToken(), 0);
            EventBus.getDefault().post(new AuthenticationEvent(activity.getAuthService().register(new User(mUsername.getText().toString(), mPassword.getText().toString()))));
        }
    }

    public void onEventMainThread(AuthenticationEvent event) {
        switch(event.getAuthenticationCode()){
            case ResponseCode.LOGIN_SUCCESS:
                Toast.makeText(activity, activity.getText(R.string.toast_register_success), Toast.LENGTH_LONG).show();
                activity.showMainMenu();
                break;
            case ResponseCode.REGISTER_USEREXISTS:
                activity.hideLoading();
                Toast.makeText(activity, activity.getText(R.string.error_register_email_registered), Toast.LENGTH_LONG).show();
                break;
            case ResponseCode.CONNECTION_ERR:
                Log.d(LOG_TAG, "CONNECTION ERROR");
                activity.hideLoading();
                Toast.makeText(activity, activity.getText(R.string.error_server_not_found), Toast.LENGTH_LONG).show();
                break;
        }
    }
}
