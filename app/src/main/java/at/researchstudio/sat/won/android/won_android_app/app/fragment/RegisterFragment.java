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
import android.os.AsyncTask;
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
import at.researchstudio.sat.won.android.won_android_app.app.webservice.constants.ResponseCode;
import at.researchstudio.sat.won.android.won_android_app.app.webservice.model.User;

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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (MainActivity) getActivity();

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.showLoading();
                new RegisterTask().execute();
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

    private class RegisterTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mUsername.getWindowToken(), 0);
            //TODO: IMPL HANDLING IF PW!=PWREPEAT
            return activity.getAuthService().register(new User(mUsername.getText().toString(), mPassword.getText().toString()));
        }

        @Override
        protected void onCancelled(Integer responseCode) {
            activity.hideLoading();
        }

        protected void onPostExecute(Integer responseCode) {
            switch(responseCode){
                case ResponseCode.LOGIN_SUCCESS:
                    activity.showMainMenu();
                    break;
                case ResponseCode.LOGIN_NOUSER:
                    //TODO: SET STUFF FOR FALSE LOGIN
                    Log.d(LOG_TAG, "USERNAME PASSWORD ERROR");
                    activity.hideLoading();
                    Toast.makeText(activity, activity.getText(R.string.error_login_failed), Toast.LENGTH_LONG).show();
                    break;
                case ResponseCode.LOGIN_CONNECTION_ERR:
                    //TODO: SET STUFF FOR CONNECTION ERRORS
                    Log.d(LOG_TAG, "CONNECTION ERROR");
                    activity.hideLoading();
                    Toast.makeText(activity, activity.getText(R.string.error_server_not_found), Toast.LENGTH_LONG).show();
                    break;
                //TODO: REFACTOR STUFF FOR REGISTER AND NOT LOGIN
            }
        }
    }
}
